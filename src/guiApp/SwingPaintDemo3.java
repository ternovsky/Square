package guiApp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class SwingPaintDemo3 extends JFrame {
    JTable dbTable;
    DefaultTableModel dtm;
    MyDb myDb;

    public static void main(String[] args) {
        final SwingPaintDemo3 swingPaintDemo3 = new SwingPaintDemo3();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                swingPaintDemo3.createAndShowGUI();
            }
        });
    }

    //Инициализация графических объектов и БД
    private void createAndShowGUI() {
        myDb = new MyDb();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent winEvt) {
                try {
                    myDb.getStmt().close();
                    myDb.getRs().close();
                    myDb.getConnection().close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });
        MyPanel myPanel = new MyPanel();
        setLocation((int)myPanel.getPreferredSize().getWidth() / 4, (int)myPanel.getPreferredSize().getHeight() / 4);
        JPanel myPanel2 = new JPanel();
        setLayout(new BorderLayout());
        setVisible(true);
        JTabbedPane jTabbedPane = new JTabbedPane();
        add(jTabbedPane);
        jTabbedPane.add("Main", myPanel);
        jTabbedPane.add("History", myPanel2);
        dbTable = new JTable();
        JScrollPane pane = new JScrollPane();
        pane.setBorder(new EmptyBorder(new Insets(0, (int)myPanel.getPreferredSize().getWidth()/6, 0, (int)myPanel.getPreferredSize().getWidth()/6)));        myPanel2.add(pane, BorderLayout.CENTER);
        pane.setPreferredSize(new Dimension((int)myPanel.getPreferredSize().getWidth(),(int)myPanel.getPreferredSize().getHeight()-30));
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.getViewport().add(dbTable, BorderLayout.CENTER);
        pack();
        databaseTable();
    }

    //Запись в таблицу на второй вкладке времени и координат
    private void addlastRow(int x, int y) {
        Object[] object = {myDb.getStringDate(), x, y};
        dtm.addRow(object);
    }

    //Метод создания таблицы на второй вкладке и заполнения данными из БД
    public void databaseTable() {
        Vector<String> values = null;
        try {
            values = myDb.getDataFromDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.reverse(values);
        Vector<String> header = new Vector<String>(4);
        header.add("Time");
        header.add("x");
        header.add("y");
        dtm = (DefaultTableModel) dbTable.getModel();
        dtm.setDataVector(values, header);
    }

    //Панель с квадратом
    class MyPanel extends JPanel {
        private int squareX = 0;
        private int squareY = 0;
        private int squareW = 20;
        private int squareH = 20;
        int currentX;
        int currentY;
        boolean isSelectSqure = false;

        public MyPanel() {
            //Координаты квадрата. Он  размещается там, где был оставлен во время предыдущего запуска.
            int last_coord[] = myDb.db_read();
            moveSquare(last_coord[0], last_coord[1]);

            //Обработчик нажатия клавиши мыши
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    checkClick(e);
                }
            });

            //Обработчик перемещения квадрата
            addMouseMotionListener(new MouseAdapter() {
                public void mouseDragged(MouseEvent e) {
                    if (isSelectSqure && (e.getX() <= getSize().getWidth() - squareW) &&
                            (e.getY() <= getSize().getHeight() - squareH) && (e.getX() > 0) && (e.getY() > 0)) {
                        moveSquare(e.getX(), e.getY());
                    }
                }
            });

            //Обработчик события, когда клавиша мыши отпускается
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (isSelectSqure) {
                        isSelectSqure = false;
                        myDb.db_write(e.getX(), e.getY());
                        addlastRow(e.getX(), e.getY());
                    }
                }
            });
        }

        //Метод проверяет, произошло ли нажатие мыши в пределах квадрата
        private void checkClick(MouseEvent e) {
            currentX = e.getX();
            currentY = e.getY();
            if ((currentX < squareX + squareW) && (currentY < squareY + squareH) && (currentX >= squareX) && (currentY >= squareY)) {
                getSize().getHeight();
                isSelectSqure = true;
            }
        }

        //Перерисовка квадрата во время его перемещения
        private void moveSquare(int x, int y) {
            if ((squareX != x) || (squareY != y)) {
                repaint(squareX, squareY, squareW, squareH);
                squareX = x;
                squareY = y;
                repaint(squareX, squareY, squareW, squareH);
            }
        }

        //Устанавливаем начальный размер окна
        @Override
        public Dimension getPreferredSize() {
            int last_coord[] = myDb.db_read();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();//разрешение экрана
            int screenHeight = screenSize.height / 2;
            int screenWidth = screenSize.width / 2;
            if (screenWidth >= last_coord[0] && screenHeight < last_coord[1] )
                return new Dimension(screenWidth, last_coord[1] + 50);
            if (screenWidth < last_coord[0] && screenHeight >= last_coord[1])
                return new Dimension(last_coord[0] + 50, screenHeight);
            if (screenWidth < last_coord[0] && screenHeight < last_coord[1])
                return new Dimension(last_coord[0] + 50, last_coord[1] + 50);
            else return new Dimension(screenWidth, screenHeight);
        }

        //Отрисовывем квадрат на панели
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.RED);
            g.fillRect(squareX, squareY, squareW, squareH);
        }
    }
}

