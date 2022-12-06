package com.ctrip.hotel.cost.infrastructure.configuration;

import com.ctrip.hotel.cost.infrastructure.client.CompareClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CompareConfig {



    @Bean("FGNotifySettleCompare")
    CompareClient fgNotifySettleCompareClient(){
        CompareClient compareClient = new CompareClient("ordernotifysettlement", "FGNotifySettleCompare", "costNotifySettleFG");
        return compareClient;
    }

}
