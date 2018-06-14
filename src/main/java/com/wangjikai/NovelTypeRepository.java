package com.wangjikai;

import com.wangjikai.po.NovelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wang.
 * @date 2018/6/8.
 * Description:
 */
@Repository
public interface NovelTypeRepository extends JpaRepository<NovelType,Long> {
}