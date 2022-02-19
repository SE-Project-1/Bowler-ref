import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class Components {

    public static JButton createJButton(String bname, JPanel buttonPanel)
    {
        JButton button = new JButton(bname);
        JPanel patronPanel = new JPanel();
        patronPanel.setLayout(new FlowLayout());
        patronPanel.add(button);
        buttonPanel.add(patronPanel);
        return button;
    }

    public static JFrame createJFrame(String fname)
    {
        JFrame window = new JFrame(fname);
        window.getContentPane().setLayout(new BorderLayout());
        ((JPanel) window.getContentPane()).setOpaque(false);
        return window;
    }

    public static JPanel createJPanel(int rows, int cols, String name)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(rows, cols));
        panel.setBorder(new TitledBorder(name));
        return panel;
    }

    public static JPanel createJPanel(String name)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBorder(new TitledBorder(name));
        return panel;
    }

    public static void setWindow(JFrame window)
    {
        window.pack();
        Dimension screenSize = (Toolkit.getDefaultToolkit()).getScreenSize();
        window.setLocation(
                ((screenSize.width) / 2) - ((window.getSize().width) / 2),
                ((screenSize.height) / 2) - ((window.getSize().height) / 2));
        window.show();
    }

}
