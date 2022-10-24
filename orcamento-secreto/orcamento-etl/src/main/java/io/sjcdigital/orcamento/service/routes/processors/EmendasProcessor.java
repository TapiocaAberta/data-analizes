package io.sjcdigital.orcamento.service.routes.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import io.sjcdigital.orcamento.model.entity.Emendas;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
public class EmendasProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        
        @SuppressWarnings("unchecked")
        List<HashMap<String, Object>> body = (List<HashMap<String, Object>>) exchange.getIn().getBody(List.class);
        
        if(!body.isEmpty()) {
            
            List<Emendas> emendas = new ArrayList<>();
            
            body.forEach(b -> {
                String id =  String.valueOf(b.get("id"));
                String codigoEmenda = String.valueOf(b.get("codigoemenda"));
                emendas.add(new Emendas(Long.parseLong(id), codigoEmenda));
            });
            
            exchange.getIn().setBody(emendas);
            
        }
        
    }

}
