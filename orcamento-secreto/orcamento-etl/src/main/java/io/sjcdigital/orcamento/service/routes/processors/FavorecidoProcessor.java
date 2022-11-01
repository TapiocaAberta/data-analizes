package io.sjcdigital.orcamento.service.routes.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
public class FavorecidoProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        @SuppressWarnings("unchecked")
        
        List<HashMap<String, Object>> body = (List<HashMap<String, Object>>) exchange.getIn().getBody(List.class);
        
        if(!body.isEmpty()) {
            
            List<Long> ids = new ArrayList<>();
            
            body.forEach(b -> {
                String id =  String.valueOf(b.get("id"));
                ids.add(Long.parseLong(id));
            });
            
            exchange.getIn().setBody(ids);
            
        }

    }

}
