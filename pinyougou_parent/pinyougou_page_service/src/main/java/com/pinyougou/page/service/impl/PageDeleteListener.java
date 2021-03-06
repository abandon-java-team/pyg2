package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.File;

/**
 * @author Steven
 * @version 1.0
 * @description com.pinyougou.page.service.impl
 * @date 2019-4-26
 */
@Component
public class PageDeleteListener implements MessageListener {
    @Value("${PAGE_SERVICE_DIR}")
    private String PAGE_SERVICE_DIR;

    @Override
    public void onMessage(Message message) {
        try {
            //接收消息
            ObjectMessage msg = (ObjectMessage) message;
            Long[] ids = (Long[]) msg.getObject();

            //删除商品详情页
            for (Long id : ids) {
                File beDelete = new File(PAGE_SERVICE_DIR + id + ".html");
                if (beDelete.exists()) {
                    //删除文件
                    boolean flag = beDelete.delete();
                    System.out.println("删除商品id为 " + id + " 的商品详情页，删除结果为：" + flag);
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
