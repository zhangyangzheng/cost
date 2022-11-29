package com.ctrip.hotel.cost.domain.settlement;

public enum SettlementItemName {
    /** 现付酒店 */
    FGHotel("601", "FGHotel"),
    /** 预付酒店 */
    PPHotel("602", "PPHotel"),
    /** 度假酒店预付单结 */
    VacationPPHotel("603", "VacationPPHotel"),
    /** 度假酒店预付月结 */
    VacationPPHotelMonth("604", "VacationPPHotelMonth"),
    /** 商旅酒店单结订单 */
    CorpPPHotel("605", "CorpPPHotel"),
    /** 酒店闪住 */
    HotelWalletPay("606", "HotelWalletPay"),
    /** 酒店供应商 */
    PPMIPHotel("607", "PPMIPHotel"),
    /** 商旅协议酒店 */
    CorpAgreementHotel("608", "CorpAgreementHotel"),
    /** NS */
    NSHotel("609", "NSHotel");

    private String value;
    private String showName;

    SettlementItemName(String value, String showName) {
        this.value = value;
        this.showName = showName;
    }

    public String getValue() {
        return value;
    }

    public String getShowName() {
        return showName;
    }

    public static SettlementItemName getEnum(String value) {
        if (value == null) {
            return null;
        }
        final SettlementItemName[] enumConstants = SettlementItemName.class.getEnumConstants();
        for (SettlementItemName enumConstant : enumConstants) {
            if (value.equalsIgnoreCase(enumConstant.getValue())) return enumConstant;
        }
        return null;
    }

    public static SettlementItemName getEnumByShowName(String name) {
        if (name == null) {
            return null;
        }
        final SettlementItemName[] enumConstants = SettlementItemName.class.getEnumConstants();
        for (SettlementItemName enumConstant : enumConstants) {
            if (name.equalsIgnoreCase(enumConstant.getShowName())) return enumConstant;
        }
        return null;
    }
}
