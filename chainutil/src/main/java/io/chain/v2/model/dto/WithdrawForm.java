package io.chain.v2.model.dto;

import io.chain.v2.model.*;

@ApiModel
public class WithdrawForm {

    @ApiModelProperty(description = "地址")
    private String address;
    @ApiModelProperty(description = "委托共识的交易hash")
    private String txHash;
    @ApiModelProperty(description = "密码")
    private String password;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
