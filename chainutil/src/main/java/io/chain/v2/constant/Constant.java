package io.chain.v2.constant;

public interface Constant {

    /**
     * CONTRACT STATUS
     */
    int NOT_FOUND = 0;

    int NORMAL = 1;

    int STOP = 2;
    String STRING = "String";
    String NOT_ENOUGH_GAS = "not enough gas";
    String CONTRACT_CONSTRUCTOR = "<init>";

    String BALANCE_TRIGGER_METHOD_NAME = "_payable";
    String BALANCE_TRIGGER_METHOD_DESC = "() return void";

    long MAX_GASLIMIT = 10000000;
    long CONTRACT_MINIMUM_PRICE = 25;

    /**
     * NRC20
     */
    String NRC20_METHOD_NAME = "name";
    String NRC20_METHOD_SYMBOL = "symbol";
    String NRC20_METHOD_DECIMALS = "decimals";
    String NRC20_METHOD_TOTAL_SUPPLY = "totalSupply";
    String NRC20_METHOD_BALANCE_OF = "balanceOf";
    String NRC20_METHOD_TRANSFER = "transfer";
    String NRC20_METHOD_TRANSFER_FROM = "transferFrom";
    String NRC20_METHOD_APPROVE = "approve";
    String NRC20_METHOD_ALLOWANCE = "allowance";
    String NRC20_EVENT_TRANSFER = "TransferEvent";
    String NRC20_EVENT_APPROVAL = "ApprovalEvent";
}
