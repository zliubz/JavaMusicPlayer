import java.awt.event.*;
import java.io.Serializable;

import javax.swing.*;
import java.awt.*;

public class checkbox implements Serializable{
    private boolean tick = false;
    JCheckBox checkBox= new JCheckBox();
    private listener l= new listener();
    class listener implements ActionListener, Serializable{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(checkBox.isSelected())tick = true;
            else tick = false;
        }
    }
    checkbox(){
        checkBox.addActionListener(l);
    }
    boolean getstate(){return tick;}
    void setstate(boolean s){tick = s;}
}

/*class test{
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        checkbox c = new checkbox();
        frame.getContentPane().add(c.checkBox);
        frame.setVisible(true);
    }
}*/
