package com.example.batchprocessing.job;

import com.example.batchprocessing.data.FeeDTO;
import com.example.batchprocessing.data.Person;
import com.example.batchprocessing.data.PersonItemProcessor;
import com.example.batchprocessing.listener.JobCompletionNotificationListener;
import com.example.batchprocessing.reader.Fee12MonthDatasetReader;
import com.example.batchprocessing.service.GestoreDbWrapper;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.JobContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableBatchProcessing
public class Fee12MonthJobConfiguration {

    private static final int MAX_THREAD = 2;
    private static final int CHUNK_SIZE = 1;

    public JobContext jobContext;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;


    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("spring_batch_async");
        taskExecutor.setConcurrencyLimit(MAX_THREAD);
        return taskExecutor;
    }

    @Bean
    public GestoreDbWrapper gestoreDb() {
        return new GestoreDbWrapper();
    }

    @Bean
    public ItemReader<FeeDTO> fee12MonthDatasetReader() {
        return new Fee12MonthDatasetReader();
    }


    @Bean
    public FlatFileItemWriter<FeeDTO> csvWriter() {
        return new FlatFileItemWriterBuilder<FeeDTO>()
                .name("csvWriter")
                .encoding("UTF-8")
                .shouldDeleteIfExists(true)
                .resource(new FileSystemResource("output/fee12MonthDataset.csv"))
                .delimited()
                .delimiter(",")
                .fieldExtractor(new BeanWrapperFieldExtractor<>())
                .build();
    }


    @Bean
    public FlatFileItemReader<Person> reader() {
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names(new String[]{"firstName", "lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                    setTargetType(Person.class);
                }})
                .build();
    }

    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(dataSource)
                .build();
    }
    // end::readerwriterprocessor[]

    // tag::jobstep[]
    @Bean
    public Job alignFee12Month(JobCompletionNotificationListener listener, Step prepareDataStep, Step processDataStep) {
        return jobBuilderFactory.get("alignFee12Month")
                .incrementer(new JobParametersIncrementer() {
                    @Override
                    public JobParameters getNext(JobParameters parameters) {

                        if (parameters.getString("run.date") == null || parameters.getString("run.date").isEmpty()) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy_HH:mm");
                            String dateParam = LocalDateTime.now().format(formatter);
                            parameters.getParameters().put("run.date", new JobParameter(dateParam));
                        }

                        return parameters;
                    }
                })
                .listener(listener)
                .flow(prepareDataStep)
//                .next(processDataStep)
                .end()
                .build();
    }


    /**
     * Read from GestoreDBWrapper and write a csv file with all the records that must be processed
     *
     * @param writer
     * @return Step
     */
    @Bean
    public Step prepareDataStep(JdbcBatchItemWriter<Person> writer) {
        return stepBuilderFactory.get("prepareDataStep")
                .<FeeDTO, FeeDTO>chunk(CHUNK_SIZE)
                .faultTolerant().skipPolicy((throwable, i) -> {
                    if (throwable instanceof org.springframework.batch.item.file.FlatFileParseException) {
                        return false;
                    }
                    return true;
                })
                .reader(fee12MonthDatasetReader())
                .writer(csvWriter())
                .build();
    }


    /**
     * Read CSV -> Process each record -> write something
     *
     * @param writer
     * @return
     */
    @Bean
    public Step processDataStep(JdbcBatchItemWriter<Person> writer) {
        return stepBuilderFactory.get("processDataStep")
                .<Person, Person>chunk(CHUNK_SIZE)
                .faultTolerant().skipPolicy((throwable, i) -> {
                    if (throwable instanceof org.springframework.batch.item.file.FlatFileParseException) {
                        return false;
                    }
                    return true;
                })
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .taskExecutor(taskExecutor()).throttleLimit(MAX_THREAD)
                .build();
    }
    // end::jobstep[]
}