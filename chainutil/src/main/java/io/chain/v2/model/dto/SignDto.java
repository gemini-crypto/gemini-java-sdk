package io.chain.v2.model.dto;

import io.chain.v2.model.*;

@ApiModel
public class SignDto {

    @ApiModelProperty(description = "地址", required = true)
    private String address;
    @ApiModelProperty(description = "明文私钥", required = false)
    private String priKey;
    @ApiModelProperty(description = "加密私钥", required = false)
    private String encryptedPrivateKey;
    @ApiModelProperty(description = "密码", required = false)
    private String password;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPriKey() {
        return priKey;
    }

    public void setPriKey(String priKey) {
        this.priKey = priKey;
    }

    public String getEncryptedPrivateKey() {
        return encryptedPrivateKey;
    }

    public void setEncryptedPrivateKey(String encryptedPrivateKey) {
        this.encryptedPrivateKey = encryptedPrivateKey;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
