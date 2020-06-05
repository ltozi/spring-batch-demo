package com.example.batchprocessing.data;

import org.springframework.batch.item.ItemProcessor;

public class FeeDTOItemProcessor implements ItemProcessor<FeeDTO, FeeDTO> {

    @Override
    public FeeDTO process(FeeDTO feeDTO) throws Exception {
        boolean newMigrated = !feeDTO.isMigrated();
        feeDTO.setMigrated(newMigrated);
        return feeDTO;
    }
}

