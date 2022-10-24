package io.sjcdigital.orcamento.service.routes.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import io.sjcdigital.orcamento.model.entity.Documentos;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
public class DocumentosProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        @SuppressWarnings("unchecked")
        List<HashMap<String, Object>> body = (List<HashMap<String, Object>>) exchange.getIn().getBody(List.class);

        if (!body.isEmpty()) {

            List<Documentos> documentos = new ArrayList<>();

            body.forEach(b -> {
                documentos.add(new Documentos((String) b.get("fase"), (String) b.get("codigodocumento")));
            });

            exchange.getIn().setBody(documentos);

        }
    }

}
