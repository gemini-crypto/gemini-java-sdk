package io.chain.v2.model.dto;

import java.math.BigInteger;
import java.util.List;

import io.chain.v2.model.*;

@ApiModel(name = "多签账户创建共识交易表单")
public class MultiSignConsensusDto extends ConsensusDto {

    @ApiModelProperty(description = "公钥集合", type = @TypeDescriptor(value = List.class, collectionElement = String.class))
    private List<String> pubKeys;
    @ApiModelProperty(description = "最小签名数")
    private int minSigns;


    public MultiSignConsensusDto() {

    }

    public List<String> getPubKeys() {
        return pubKeys;
    }

    public void setPubKeys(List<String> pubKeys) {
        this.pubKeys = pubKeys;
    }

    public int getMinSigns() {
        return minSigns;
    }

    public void setMinSigns(int minSigns) {
        this.minSigns = minSigns;
    }
}
