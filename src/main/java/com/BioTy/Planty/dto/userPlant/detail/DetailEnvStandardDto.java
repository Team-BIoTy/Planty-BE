package com.BioTy.Planty.dto.userPlant.detail;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DetailEnvStandardDto {
    private Range temperature;
    private Range light;
    private Range humidity;

    @Getter
    @AllArgsConstructor
    public static class Range{
        private Integer min;
        private Integer max;
    }

}
