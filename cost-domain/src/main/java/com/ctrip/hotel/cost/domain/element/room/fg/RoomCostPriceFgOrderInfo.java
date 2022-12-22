package com.ctrip.hotel.cost.domain.element.room.fg;

import com.ctrip.hotel.cost.domain.core.Factor;
import com.ctrip.hotel.cost.domain.element.AbstractElementFg;
import com.ctrip.hotel.cost.domain.element.room.RoomCostPrice;
import com.ctrip.hotel.cost.domain.scene.CostItemType;
import hotel.settlement.common.DateHelper;
import hotel.settlement.common.QConfigHelper;
import hotel.settlement.common.enums.DateDiffType;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-07 19:48
 */
@Data
public class RoomCostPriceFgOrderInfo extends AbstractElementFg implements RoomCostPrice {

    private BigDecimal cost;
    private Calendar eTA;
    private Calendar eTD;

    // 订单属性
    private Calendar orderETA;
    private Calendar realETD;
    private Integer hourAdjuest;
    private String hourRoom;

    // 计算结果
    private BigDecimal result;

    @Override
    public Calendar orderBeginDate() {
        return orderETA;
//        return this.getEta();
    }

    @Override
    public Calendar orderEndDate() {
        return this.getRealETD();
    }

    @Override
    public Calendar elementBeginDate() {
        return eTA;
//        return this.getETA();
    }

    @Override
    public Calendar elementEndDate() {
        return this.getETD();
    }

    @Override
    public Factor cost() {
        return new Factor("cost", this.getCost());
    }

    /**
     * hourAdjuest代表房间审核是否提前离店, 如果提前离店，最后一天的房费不能收一天的钱，退还半天（-12/24）
     * 最后一天的判断条件：eTA < realETD <= eTD
     * @return
     */
    @Override
    public Factor days() {
        int orderDays = countDays();
        if (this.getHourAdjuest() != null && this.getHourAdjuest() != 0) {
            if (DateHelper.dateDiff(DateDiffType.Day, this.getRealETD(), this.getETA()) > 0
                    && DateHelper.dateDiff(DateDiffType.Day, this.getRealETD(), this.getETD()) <= 0) {
                BigDecimal days = BigDecimal.valueOf(orderDays).add(
                        BigDecimal.valueOf(this.getHourAdjuest()).divide(BigDecimal.valueOf(24), 4, RoundingMode.HALF_UP)
                );
                return new Factor("days", days);
            }

        }
        return new Factor("days", new BigDecimal(orderDays));
    }

    @Override
    public Factor quantity() {
        return new Factor("quantity", BigDecimal.ONE);// 审核离店是房间维度，所以数量是1间
    }

    @Override
    public String costItemName() {
        return CostItemType.ROOM_COST_PRICE_FG.getCostItemName();
    }

    @Override
    public String currency() {
        return null;
    }

    @Override
    public BigDecimal exchangeRate() {
        return null;
    }

    @Override
    public String formula() {
        return QConfigHelper.getSwitchConfigByKey(CostItemType.ROOM_COST_PRICE_FG.getFormulaQConfigKey(), CostItemType.ROOM_COST_PRICE_FG.getFormula());
    }

    @Override
    public List<Factor> factors() {
        return Arrays.asList(
                cost(),
                days(),
                quantity()
        );
    }

    @Override
    public BigDecimal result() {
        return this.getResult();
    }

    @Override
    public void listener() throws Exception {
        this.setResult(computing(this));
    }
}
