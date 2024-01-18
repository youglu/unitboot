/*
 * Copyright (C) 2005 - 2030 YGSoft.Inc All Rights Reserved.
 * YGSoft.Inc PROPRIETARY/CONFIDENTIAL.Use is subject to license terms.
 */
package org.union.sbp.springfragment.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * 路由工具.<br>
 *
 * @author lhj <br>
 * @version 1.0.0 2021年5月13日<br>
 * @see
 * @since JDK 1.8.0
 */
public final class ReflectUtil {
	/**
	 * 日志.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectUtil.class);

	/**
	 * 反射获得属性的值.
	 * @param fieldName
	 * @param instance
	 * @param fieldClass_
	 * @param <T>
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	public static <T> T getFieldValue(final String fieldName,Object instance,Class...fieldClass_) throws NoSuchFieldException, IllegalAccessException {
		Class fieldClass = null;
		if(null != fieldClass_ && fieldClass_.length > 0){
			fieldClass = fieldClass_[0];
		}
		if(null == fieldClass && null != instance){
			fieldClass = instance.getClass();
		}
		if(fieldClass != null) {
			final Field field = fieldClass.getDeclaredField(fieldName);
			field.setAccessible(true);
			return (T) field.get(instance);
		}
		return null;
	}

	/**
	 * 反射设置属性值.
	 * @param fieldName
	 * @param instance
	 * @param fieldValue
	 * @param fieldClass_
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	public static void setFieldValue(final String fieldName,Object instance,Object fieldValue, Class...fieldClass_) throws NoSuchFieldException, IllegalAccessException {
		Class fieldClass = null;
		if(null != fieldClass_ && fieldClass_.length > 0){
			fieldClass = fieldClass_[0];
		}
		if(null == fieldClass && null != instance){
			fieldClass = instance.getClass();
		}
		if(fieldClass != null) {
			Field field = findField(fieldName,fieldClass);
			field.setAccessible(true);
			field.set(instance, fieldValue);
		}
	}
	/**
	 * @Description: 根据给定的属性名获得属性对象.
	 *
	 * @author youg
	 * @since jdk1.8
	 * @return Field
	 */
	private static Field findField(final String fieldName, Class fieldClass)  {
		if(fieldClass != null) {
			Field field = null;
			try {
				field = fieldClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				LOGGER.error("从:{} 获得属性:{} 发生异常:{}",fieldClass,fieldName,e.getMessage());
			}
			while(null == field && fieldClass != Object.class){
				fieldClass = fieldClass.getSuperclass();
				try {
					field = fieldClass.getDeclaredField(fieldName);
				} catch (NoSuchFieldException e) {
					LOGGER.error("从:{} 获得属性:{} 发生异常:{}",fieldClass,fieldName,e.getMessage());
				}
			}
			return field;
		}
		return null;
	}
	/**
	 * @Description: 根据给定的方法名获得方法对象.
	 *
	 * @author youg
	 * @since jdk1.8
	 * @return Method
	 */
	private static Method findMethod(final String methodName, Class methodClass,Class...paramTypes)   {
		Method method = null;
		if(methodClass != null) {

				try {
					method = methodClass.getDeclaredMethod(methodName,paramTypes);
				} catch (NoSuchMethodException e) {
					LOGGER.error("从:{} 获得属方法:{} 发生异常:{}",methodClass,methodName,e.getMessage());
				}
				while(null == method && methodClass != Object.class){
					methodClass = methodClass.getSuperclass();
					try {
						method = methodClass.getDeclaredMethod(methodName,paramTypes);
					} catch (NoSuchMethodException e) {
						LOGGER.error("从:{} 获得属方法:{} 发生异常:{}",methodClass,methodName,e.getMessage());
					}
				}

		}
		return method;
	}
	/**
	 * 反射调用方法.
	 *
	 * @param methodName methodName
	 * @param instance instance
	 * @param param param
	 * @param methodClass_ methodClass_
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 */
	public static void callMethod(final String methodName,Object instance,Object param,Class[] paramTypes, Class...methodClass_) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Class methodClass = null;
		if(null != methodClass_ && methodClass_.length > 0){
			methodClass = methodClass_[0];
		}
		if(null == methodClass && null != instance){
			methodClass = instance.getClass();
		}
		if(methodClass != null) {
			final Method method = findMethod(methodName,methodClass, paramTypes);
			final boolean accessble = method.isAccessible();
			try {
				method.setAccessible(true);
				method.invoke(instance, new Object[]{param});
			}catch (Error error){
				throw error;
			}finally {
				method.setAccessible(accessble);
			}
		}
	}
}
