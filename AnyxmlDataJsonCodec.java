package org.yangcentral.yangkit.data.codec.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.yangcentral.yangkit.common.api.validate.ValidatorResultBuilder;
import org.yangcentral.yangkit.data.api.model.AnyxmlData;
import org.yangcentral.yangkit.data.api.model.YangData;
import org.yangcentral.yangkit.model.api.stmt.Anyxml;
import org.dom4j.Document;

public class AnyxmlDataJsonCodec extends YangDataJsonCodec<Anyxml, AnyxmlData> {
    protected AnyxmlDataJsonCodec(Anyxml schemaNode) {
        super(schemaNode);
    }

    @Override
    protected AnyxmlData buildData(JsonNode element, ValidatorResultBuilder validatorResultBuilder) {
//        return (AnyxmlData) YangDataBuilderFactory.getBuilder().getYangData(getSchemaNode(),
//                DocumentHelper.createDocument(element));
        return null;
    }

    @Override
    protected void buildElement(JsonNode element, YangData<?> yangData) {
        AnyxmlData anyxmlData = (AnyxmlData) yangData;
        Document document = anyxmlData.getValue();
//        for(Element child: document.getRootElement().elements()){
//            child.detach();
//            element.add(child);
//        }

    }

}
