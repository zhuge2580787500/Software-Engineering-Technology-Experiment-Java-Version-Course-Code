package com.ems;

/**
 * 奖金计算接口（BonusCalculable）
 * <p>
 * 定义奖金计算的行为契约，供需要计算奖金的人员类型实现。
 * 使用接口而非抽象类，是因为"可计算奖金"是一种能力（capability），
 * 不是一种"是一种"（is-a）关系——并非所有员工都能计算奖金（如兼职员工），
 * 但经理和全职员工可以。接口支持多实现，符合"面向接口编程"原则。
 * </p>
 *
 * @author wyh
 * @since 1.0
 */
public interface BonusCalculable {

    /**
     * 计算总奖金
     *
     * @return 奖金总额
     */
    double calculateBonus();
}
