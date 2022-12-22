/**
 * <b>
 *     1、*repositoryImpl用来实现cost-domain的repository定义。
 *     2、如果是向cost-job、cost-service、cost-web等模块透传DB模型、三方库model等情况，repository接口定义写在此处，接口实现在impl包下
 *     eg:{@link com.ctrip.hotel.cost.repository.OrderAuditFgMqRepository}
 *     {@link com.ctrip.hotel.cost.repository.impl.OrderAuditFgMqRepositoryImpl}
 * </b>
 * @author yangzhengzhang
 * @date 2022-11-17 14:44
 */
package com.ctrip.hotel.cost.repository;