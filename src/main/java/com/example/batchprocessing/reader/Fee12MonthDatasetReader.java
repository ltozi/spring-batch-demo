package com.example.batchprocessing.reader;

import com.example.batchprocessing.data.FeeDTO;
import com.example.batchprocessing.service.GestoreDbWrapper;
import it.ubiquity.gestoreprofilazione.dataobjects.Esito;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class Fee12MonthDatasetReader implements ItemReader<FeeDTO> {


//    @Autowired //TODO add @Bean in config class
//    private final GestoreDbWrapper nexiDbService;

    private int itemIndex = 0;
    private List<FeeDTO> data;

    /**
     * Client db Nexi
     */
    @Autowired
    private GestoreDbWrapper gestoreDb;

    public Fee12MonthDatasetReader() {

    }


    @Override
    public FeeDTO read() throws Exception {

        //Do web service call only once
        if (isInitialized()) {
            data = fetchDataFromNexiDb();
        }

        FeeDTO nextStudent = null;

        if (itemIndex < data.size()) {
            nextStudent = data.get(itemIndex);

            synchronized(this) {
                itemIndex++;
            }


        }

        return nextStudent;
    }

    private boolean isInitialized() {
        return this.data == null;
    }

    private List<FeeDTO> fetchDataFromNexiDb() {
        //TODO read from GestoreDbWrapper
//        nexiDbService.

        //TODO fetch data as done here:
        // https://bitbucket.nexicloud.it/projects/sms/repos/gestorebatchcartasi/browse/gestoreBatch-core/src/main/java/it/ubiquity/gestorebatch/icbpi/FlussoBase.java?at=da6cfb5e663caf3e61bcce65ca5ee21837386673#1179
//        Esito esito = gestoreDb.caricaAnagraficheAllineamentoFeeDodiciMesi(0, 1, 1, 1);

        data = new ArrayList<>();

        //Data stub
        for (int i = 1; i <= 100_000; i++) {
            FeeDTO feeDTO = new FeeDTO();
            feeDTO.id = Long.parseLong(String.valueOf(i));
            feeDTO.pan = String.valueOf(UUID.randomUUID()).substring(16);
            feeDTO.mobileNumber = "+393289499400";

            data.add(feeDTO);
        }

        return data;
    }
}
