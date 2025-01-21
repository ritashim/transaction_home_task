package moe.rtc.transaction.controller;

import lombok.Getter;

public class TransactionControllerErrorCodes {
    public static final String ID_IS_NULL = "C_TRANSACTION_NID";
    public static final String ACCOUNT_IS_EMPTY = "C_TRANSACTION_NAC";
    public static final String AMOUNT_IS_NULL = "C_TRANSACTION_NAM";
    public static final String TRANSACTION_TYPE_IS_EMPTY = "C_TRANSACTION_NTT";
}
