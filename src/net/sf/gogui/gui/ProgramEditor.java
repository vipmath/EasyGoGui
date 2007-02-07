//----------------------------------------------------------------------------
// $Id$
//----------------------------------------------------------------------------

package net.sf.gogui.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Locale;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/** Dialog for displaying and editing a program. */
public class ProgramEditor
    implements ObjectListEditor.ItemEditor
{
    public Object editItem(Component parent, Object object)
    {
        return editItem(parent, "Edit Program", (Program)object);
    }

    public Program editItem(Component parent, String title, Program program)
    {
        JPanel panel = new JPanel(new BorderLayout(GuiUtil.SMALL_PAD, 0));
        m_panelLeft = new JPanel(new GridLayout(0, 1, 0, GuiUtil.PAD));
        panel.add(m_panelLeft, BorderLayout.WEST);
        m_panelRight =
            new JPanel(new GridLayout(0, 1, 0, GuiUtil.PAD));
        panel.add(m_panelRight, BorderLayout.CENTER);
        m_name = createEntry("Name", 20, program.m_name);
        createCommandEntry(program.m_command);
        JOptionPane optionPane = new JOptionPane(panel,
                                                 JOptionPane.PLAIN_MESSAGE,
                                                 JOptionPane.OK_CANCEL_OPTION);
        m_dialog = optionPane.createDialog(parent, title);
        boolean done = false;
        while (! done)
        {
            m_dialog.addWindowListener(new WindowAdapter() {
                    public void windowActivated(WindowEvent e) {
                        m_name.requestFocusInWindow();
                    }
                });
            m_dialog.setVisible(true);
            Object value = optionPane.getValue();
            if (! (value instanceof Integer)
                || ((Integer)value).intValue() != JOptionPane.OK_OPTION)
                return null;
            done = validate(parent);
        }
        String newName = m_name.getText().trim();
        String newCommand = m_command.getText().trim();
        Program newProgram = new Program(newName, newCommand);
        m_dialog.dispose();
        return newProgram;
    }

    public String getItemLabel(Object object)
    {
        return ((Program)object).m_name;
    }

    public Object cloneItem(Object object)
    {
        return new Program((Program)object);
    }

    /** Serial version to suppress compiler warning.
        Contains a marker comment for serialver.sourceforge.net
    */
    private static final long serialVersionUID = 0L; // SUID

    private JPanel m_panelLeft;

    private JPanel m_panelRight;

    private JTextField m_name;

    private JTextField m_command;

    private JDialog m_dialog;

    private JTextField createEntry(String labelText, int cols, String text)
    {
        m_panelLeft.add(createEntryLabel(labelText));
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JTextField field = new JTextField(cols);
        panel.add(field);
        m_panelRight.add(panel);
        return field;
    }

    private JComponent createEntryLabel(String text)
    {
        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        JLabel label = new JLabel(text + ":");
        label.setAlignmentY(Component.CENTER_ALIGNMENT);
        box.add(label);
        return box;
    }
    private void createCommandEntry(String text)
    {
        m_panelLeft.add(createEntryLabel("Command"));
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        m_command = new JTextField(20);
        m_command.setText(text);
        panel.add(m_command);
        panel.add(GuiUtil.createSmallFiller());
        JButton button = new JButton();
        panel.add(button);
        button.setIcon(GuiUtil.getIcon("document-open-16x16", "Browse"));
        button.setToolTipText("Browse for Go program");
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    File file =
                        SimpleDialogs.showOpen(m_dialog, "Select Go Program");
                    if (file == null)
                        return;
                    String text = file.toString();
                    if (text.indexOf(' ') >= 0)
                        text = "\"" + text + "\"";        
                    String fileNameToLower =
                        file.getName().toLowerCase(Locale.ENGLISH);
                    if (fileNameToLower.startsWith("gnugo"))
                    {
                        String message =
                            "Append option '--mode gtp' for GNU Go?";
                        if (SimpleDialogs.showQuestion(m_dialog, message))
                            text = text + " --mode gtp";
                    }
                    m_command.setText(text);
                    m_command.setCaretPosition(text.length());
                    m_command.requestFocusInWindow();
                }
            });
        m_panelRight.add(panel);
    }

    private boolean validate(Component parent)
    {
        if (m_name.getText().trim().equals(""))
        {
            SimpleDialogs.showError(parent, "Name cannot be empty");
            return false;
        }
        if (m_command.getText().trim().equals(""))
        {
            SimpleDialogs.showError(parent, "Command cannot be empty");
            return false;
        }
        return true;
    }
}
