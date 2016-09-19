package com.tools.guiDemo;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Created by edwinchang on 2016-9-15.
 */
public class FrameInOut extends Frame implements ActionListener
{
    JButton btn1,btn2,btn3,btn4;
    JTextArea ta1,ta2;
    JPanel p1,p2,p3;
    FrameInOut()
    {
        super("Java小程序");
        this.setFont(new Font("隶体",Font.BOLD,100));
        this.setBackground(Color.pink);
        /*初始化各个按钮*/
        btn1=new JButton("水仙花数");
        btn2=new JButton("Roll点");
        btn3=new JButton("完全数");
        btn4=new JButton("退出");
        /*初始化文本域*/
        /*初始化文本域的大小，行列数*/
        ta1=new JTextArea(10,25);
        ta2=new JTextArea(10,25);
        /*初始化面板，将个空间加入容器*/
        p1=new JPanel();
        p2=new JPanel();
        p3=new JPanel();
        p1.add(btn1);
        p1.add(btn2);
        p1.add(btn3);
        p1.add(btn4);
        p2.add(ta1);
        p2.add(ta2);
        add(p1);
        add(p2);
        add(p3);
        setLayout(new FlowLayout());
        /*设置面板背景色*/
        p1.setBackground(Color.red);
        /*各个按钮注册事件监听器*/
        btn1.addActionListener(this);
        btn2.addActionListener(this);
        btn3.addActionListener(this);
        btn4.addActionListener(this);
        setSize(600,360);//设置界面尺寸
        setVisible(true);
    }
    /*重载ActionListener接口的方法，实现各按钮名副其实的功能*/
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource()==btn1)
        {
            ta1.setText(null);
            ta1.setForeground(Color.blue);
            ta1.setFont(new Font("隶体",Font.BOLD,14));
            int num,i,j,k,l=1;
            for(num=100;num<=999;num++)
            {
                i=num/100;
                j=(num-i*100)/10;
                k=num%10;
                if(num==Math.pow(i,3)+Math.pow(j,3)+Math.pow(k,3))
                {
                    ta1.append("第"+l+"个水仙花数是："+num+"\n");
                    l++;
                }
            }
        }
        if(e.getSource()==btn2)
        {
            int num2=(int)(Math.random()*100);//Math.random返回的是0-1之间的一个double型数，*100之后范围扩展为0-100之间的任意数
            ta2.setText(null);
            ta2.setForeground(Color.blue);
            ta2.setFont(new Font("楷体",Font.BOLD,16));
            ta2.append("您roll的点数是："+num2);
        }
        if(e.getSource()==btn3)
        {
            int k=1;
            ta1.setText(null);
            ta1.setForeground(Color.red);
            ta1.setFont(new Font("楷体",Font.BOLD,16));
            for(int i=1;i<=10000;i++)
            {
                int y=0;
                for(int j=1;j<i;j++)
                    if(i%j==0) y+=j;
                if(y==i)
                {
                    ta1.append("第"+k+"个完全数是："+i+"\n");
                    k++;
                }
            }
        }
        if(e.getSource()==btn4)
        {
            dispose();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new FrameInOut();
    }
}
