package io.chain.v2.model.dto;

import java.math.BigInteger;

import io.chain.v2.model.*;

@ApiModel
public class WithDrawDto {
    @ApiModelProperty(description = "地址")
    private String address;
    @ApiModelProperty(description = "委托共识交易的hash")
    private String depositHash;
    @ApiModelProperty(description = "手续费单价" ,required = false)
    private BigInteger price;
    @ApiModelProperty(description = "交易输入信息")
    private CoinFromDto input;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDepositHash() {
        return depositHash;
    }

    public void setDepositHash(String depositHash) {
        this.depositHash = depositHash;
    }

    public CoinFromDto getInput() {
        return input;
    }

    public void setInput(CoinFromDto input) {
        this.input = input;
    }

    public BigInteger getPrice() {
        return price;
    }

    public void setPrice(BigInteger price) {
        this.price = price;
    }
}
