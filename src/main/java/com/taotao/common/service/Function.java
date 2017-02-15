package com.taotao.common.service;

public interface Function<T, E> {
	
	T callback(E e);
	
}
