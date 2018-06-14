package com.wangjikai;

import com.wangjikai.po.Website;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wang.
 * @date 2018/5/26.
 * Description:
 */
@Repository
public interface WebsiteRepository extends JpaRepository<Website,Integer> {
}