package com.wangjikai.util;


import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import com.google.common.hash.PrimitiveSink;
import com.wangjikai.po.Novel;
import org.apache.commons.lang3.CharSet;

/**
 * @author wang.
 * @date 2018/6/1.
 * Description:
 * 布隆过滤器，用于检索一个元素是否在另一个元素中判断元素是否存在
 * 集合的快速的概率算法，可能会出现错误判断，但不会漏掉判断。即：过
 * 滤器判断元素不在集合，那肯定不在，如果判断元素存在集合中，有一定
 * 的概率判断错误。不适合零错误的场合，适合于能容忍低错误率的应用场合。
 * 基本思想：
 */
public class BloomFilterTest {

    public static void main(String[] args) {
        Funnel<Novel> novelFunnel = new Funnel<Novel>() {
            @Override
            public void funnel(Novel novel, PrimitiveSink primitiveSink) {
                primitiveSink.putString(novel.getListUrl(), Charsets.UTF_8);
            }
        };
        BloomFilter<Novel> bloomFilter = BloomFilter.create(novelFunnel,92000);

    }
}
