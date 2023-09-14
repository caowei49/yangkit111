package org.yangcentral.yangkit.data.codec.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.yangcentral.yangkit.common.api.QName;
import org.yangcentral.yangkit.common.api.exception.ErrorMessage;
import org.yangcentral.yangkit.common.api.exception.ErrorTag;
import org.yangcentral.yangkit.common.api.validate.ValidatorRecordBuilder;
import org.yangcentral.yangkit.common.api.validate.ValidatorResultBuilder;
import org.yangcentral.yangkit.data.api.builder.YangDataBuilderFactory;
import org.yangcentral.yangkit.data.api.model.*;
import org.yangcentral.yangkit.model.api.stmt.Leaf;
import org.yangcentral.yangkit.model.api.stmt.SchemaNode;
import org.yangcentral.yangkit.model.api.stmt.YangList;
import java.util.ArrayList;
import java.util.List;

public class ListDataJsonCodec extends YangDataJsonCodec<YangList, ListData> {
    protected ListDataJsonCodec(YangList schemaNode) {
        super(schemaNode);
    }

    @Override
    protected ListData buildData(JsonNode element, ValidatorResultBuilder validatorResultBuilder) {
        //key
        List<LeafData> keyDataList = new ArrayList<>();
        List<Leaf> keys = getSchemaNode().getKey().getkeyNodes();
        for(Leaf key:keys){
            JsonNode keyElement = element.get(key.getArgStr());
            if(keyElement == null){
                ValidatorRecordBuilder<String, JsonNode> recordBuilder = new ValidatorRecordBuilder<>();
                recordBuilder.setErrorTag(ErrorTag.MISSING_ELEMENT);
                recordBuilder.setErrorPath(element.toString());
                recordBuilder.setBadElement(element);
                recordBuilder.setErrorMessage(new ErrorMessage("missing key:" + key.getIdentifier().getLocalName()));
                validatorResultBuilder.addRecord(recordBuilder.build());
                return null;
            }
            YangDataJsonCodec jsonCodec = YangDataJsonCodec.getInstance(key);
            LeafData keyData = (LeafData) jsonCodec.deserialize(keyElement,validatorResultBuilder);
            keyDataList.add(keyData);
        }
        ListData listData = (ListData) YangDataBuilderFactory.getBuilder().getYangData(getSchemaNode(), keyDataList);
        return listData;
    }

    @Override
    protected void buildElement(JsonNode element, YangData<?> yangData) {

    }

    @Override
    protected void serializeChildren(JsonNode element, YangDataContainer yangDataContainer) {
        ListData listData = (ListData) yangDataContainer;
        YangList yangList = listData.getSchemaNode();
        //serialize key firstly
        for(LeafData key:listData.getKeys()){
            YangDataJsonCodec jsonCodec = getInstance(key.getSchemaNode());
            JsonNode childElement = jsonCodec.serialize(key);
            String jsonKey = key.getQName().getLocalName();
            ((ObjectNode)element).put(jsonKey,childElement);

        }
        List<SchemaNode> schemaChildren = yangList.getTreeNodeChildren();
        for(SchemaNode dataChild:schemaChildren){
            if(dataChild instanceof Leaf){
                Leaf leaf = (Leaf) dataChild;

                if(leaf.isKey()){
                    continue;
                }
            }
            List<YangData<?>> childData= yangDataContainer
                    .getDataChildren(dataChild.getIdentifier());
            for(YangData<?> childDatum:childData){
                if(childDatum.isDummyNode()){
                    continue;
                }
                YangDataJsonCodec jsonCodec = getInstance(childDatum.getSchemaNode());
                JsonNode childElement = jsonCodec.serialize(childDatum);
                String moduleName = childDatum.getSchemaNode().getContext().getCurModule().getMainModule().getArgStr();

                ((ObjectNode)element).put(moduleName+":"+childDatum.getQName().getLocalName(),childElement);

            }
        }
    }


}
