package com.ctrip.hotel.cost.configuration;

import com.ctrip.hotel.cost.client.CompareClient;
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
