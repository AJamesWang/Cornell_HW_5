package FP;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import datastructures.LinkedList;
import datastructures.Tuple;

public class MapGUI extends JFrame {
    private static int ROAD_ARROW_RATIO = 10;
    MapGUI mapGUI;
    Map map;
    MapPanel mapPanel;
    ButtonPanel buttonPanel;
    Listener listener;

    public MapGUI(Map map) {
        super("Main Frame");
        this.mapGUI = this;
        this.map = map;
        this.listener = new Listener();
        addMouseListener(listener);
        addKeyListener(listener);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        this.mapPanel = new MapPanel();
        this.add(mapPanel, BorderLayout.CENTER);

        this.buttonPanel = new ButtonPanel();
        this.add(buttonPanel, BorderLayout.SOUTH);

        this.pack();
        this.setVisible(true);
    }

    class MapPanel extends JPanel {
        // button and associated village
        LinkedList<Tuple<JButton, Village>> villages;

        public MapPanel() {
            super();
            setLayout(null);
            this.villages = new LinkedList<Tuple<JButton, Village>>();
            makeVillages();
            addKeyListener(listener);
        }

        public void refreshVillages() {
            for (int i = 0; i < villages.getLength(); i++) {
                JButton button = villages.get(i).getA();
                button.getParent().remove(button);
            }
            villages = new LinkedList<Tuple<JButton, Village>>();
            makeVillages();
            repaint();
        }

        private JButton makeVillageButton(Village village) {
            ImageIcon icon = new ImageIcon("button.png");
            JButton out = new JButton(icon);
            out.setOpaque(false);
            // out.setContentAreaFilled(false);
            out.setBorderPainted(false);
            out.setFocusPainted(false);

            out.setBounds(village.getX(), village.getY(), icon.getIconWidth(), icon.getIconHeight());
            out.addActionListener(listener);
            out.addKeyListener(listener);
            return out;
        }

        private void makeVillages() {
            MyList<Village> villages = map.getVillages();
            for (int i = 0; i < villages.getSize(); i++) {
                Village village = villages.get(i);
                if (village != null) {
                    JButton villageButton = makeVillageButton(village);

                    this.villages.add(new Tuple<JButton, Village>(villageButton, village));
                    add(villageButton);
                }
            }

        }

        public Dimension getPreferredSize() {
            return new Dimension(Map.DEFAULT_WIDTH, Map.DEFAULT_HEIGHT);
        }

        private void paintRoads(Graphics g) {
            MyList<Road> roads = map.getRoads();
            LinkedList<Tuple<Integer, Tuple<Tuple<Integer, Integer>, Tuple<Integer, Integer>>>> arrows = new LinkedList();
            for (int i = 0; i < roads.getSize(); i++) {
                Road road = roads.get(i);

                if (road != null) {
                    int x1 = map.getVillage(road.getFromID()).getX() + Village.DIAMETER / 2;
                    int y1 = map.getVillage(road.getFromID()).getY() + Village.DIAMETER / 2;
                    int x2 = map.getVillage(road.getToID()).getX() + Village.DIAMETER / 2;
                    int y2 = map.getVillage(road.getToID()).getY() + Village.DIAMETER / 2;

                    int x3 = x2 - ((x2 - x1) / ROAD_ARROW_RATIO);
                    int y3 = y2 - ((y2 - y1) / ROAD_ARROW_RATIO);
                    arrows.add(new Tuple(road.getWeight(), new Tuple(new Tuple(x3, y3), new Tuple(x2, y2))));

                    ((Graphics2D) g).setStroke(new BasicStroke(road.getWeight()));
                    g.setColor(Color.black);
                    g.drawLine(x1, y1, x2, y2);
                }
            }
            for (int i = 0; i < arrows.getLength(); i++) {
                int weight = arrows.get(i).getA();
                Tuple<Integer, Integer> coor3 = arrows.get(i).getB().getA();
                Tuple<Integer, Integer> coor2 = arrows.get(i).getB().getB();
                int x3 = coor3.getA();
                int y3 = coor3.getB();
                int x2 = coor2.getA();
                int y2 = coor2.getB();

                ((Graphics2D) g).setStroke(new BasicStroke(weight));
                g.setColor(Color.red);
                g.drawLine(x3, y3, x2, y2);

            }
        }

        // @todo: decide whether to keep or no
        @Override
        // not in paintComponent b/c I want to paint over the buttons
        protected void paintChildren(Graphics g) {
            super.paintChildren(g);
            paintRoads(g);
        }

        public Village getVillage(JButton button) {
            Tuple<JButton, Village> shell = new Tuple<JButton, Village>(button, null);
            return villages.get(villages.getIndex(shell)).getB();
        }
    }

    class ButtonPanel extends JButton {
        JButton addVillage;
        JButton delVillage;
        JButton addRoad;
        JButton delRoad;

        public ButtonPanel() {
            super();
            setLayout(new GridLayout());

            addVillage = new JButton("Add Village (q)");
            addVillage.addActionListener(listener);
            add(addVillage);

            delVillage = new JButton("Delete Village (w)");
            delVillage.addActionListener(listener);
            add(delVillage);

            addRoad = new JButton("Add Road (e)");
            addRoad.addActionListener(listener);
            add(addRoad);

            delRoad = new JButton("Delete Road (r)");
            delRoad.addActionListener(listener);
            add(delRoad);

            addKeyListener(listener);
        }
    }

    class Listener implements ActionListener, MouseListener, KeyListener {
        private static final int NEUTRAL = 0;
        private static final int ADD_VILLAGE = 11;
        private static final int DEL_VILLAGE = 21;
        private static final int ADD_ROAD = 12;
        private static final int DEL_ROAD = 22;

        int state;
        Village prev;

        public Listener() {
            this.state = NEUTRAL;
            this.prev = null;
        }

        private LinkedList<Village> extractVillages(LinkedList<Road> roads, boolean in) {
            LinkedList<Village> villages = new LinkedList<Village>();
            for (int i = 0; i < roads.getLength(); i++) {
                if (in) {
                    villages.add(map.getVillage(roads.get(i).getFromID()));
                } else {
                    villages.add(map.getVillage(roads.get(i).getToID()));
                }
            }
            return villages;
        }

        private void showVillageInfo(ActionEvent e) {
            Village village = mapPanel.getVillage((JButton) e.getSource());
            String name = village.getName();
            int id = village.getID();
            LinkedList<Road> roadsIn = convertMyList(village.getRoadsIn());
            LinkedList<Village> in = extractVillages(roadsIn, true);
            LinkedList<Road> roadsOut = convertMyList(village.getRoadsOut());
            LinkedList<Village> out = extractVillages(roadsOut, false);
            String format = "Village Name:\t%s\nVillage ID:\t %s\nInbound Roads From:\n\t%s\nOutbound Roads To:\n\t%s";
            ImageIcon icon = new ImageIcon("village.jpg");

            String message = String.format(format, name, id, in.toString(), out.toString());
            JOptionPane.showMessageDialog(mapGUI, message, "Village Info", JOptionPane.PLAIN_MESSAGE, icon);

            mapPanel.refreshVillages();
        }

        private void delVillage(ActionEvent e) {
            Village village = mapPanel.getVillage((JButton) e.getSource());
            int decision = JOptionPane.showConfirmDialog(mapGUI, "Reroute Roads?", "Road", JOptionPane.YES_NO_OPTION);
            if (decision == JOptionPane.YES_OPTION) {
                map.removeVillage2(village.getID());
            } else if (decision == JOptionPane.NO_OPTION) {
                map.removeVillage(village.getID());
            } else {
                throw new RuntimeException("UHHHH");
            }
            mapPanel.refreshVillages();
            state = NEUTRAL;
        }

        private void addRoad(ActionEvent e) {
            if (prev == null) {
                prev = mapPanel.getVillage((JButton) e.getSource());
            } else {
                Village next = mapPanel.getVillage((JButton) e.getSource());
                if (next == prev) {
                    JOptionPane.showMessageDialog(mapGUI, "That would be a pointless road", "Foolish Mortal",
                                    JOptionPane.WARNING_MESSAGE);
                } else {
                    int weight = Integer
                                    .parseInt((String) JOptionPane.showInputDialog(mapGUI, "How much is the toll?"));
                    map.addRoad(prev.getID(), next.getID(), weight);
                }
                prev = null;
                state = NEUTRAL;
            }
            mapPanel.refreshVillages();
        }

        private void delRoad(ActionEvent e) {
            if (prev == null) {
                prev = mapPanel.getVillage((JButton) e.getSource());
            } else {
                Village next = mapPanel.getVillage((JButton) e.getSource());
                // @todo_0: uncomment after removeRoad is added
                // System.out.println("not implemented");
                map.removeRoad(prev.getID(), next.getID());
                repaint();
                prev = null;
                state = NEUTRAL;
            }
            mapPanel.refreshVillages();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // if is button
            if (e.getSource().getClass().equals((new JButton()).getClass())) {
                JButton b = (JButton) e.getSource();
                if (b.equals(buttonPanel.addVillage)) {
                    state = ADD_VILLAGE;
                    prev = null;
                } else if (b.equals(buttonPanel.delVillage)) {
                    state = DEL_VILLAGE;
                    prev = null;
                } else if (b.equals(buttonPanel.addRoad)) {
                    state = ADD_ROAD;
                    prev = null;
                } else if (b.equals(buttonPanel.delRoad)) {
                    state = DEL_ROAD;
                    prev = null;
                } else { // one of the villages was clicked
                    switch (this.state) {
                        case (NEUTRAL):
                            showVillageInfo(e);
                            break;
                        case (ADD_VILLAGE):
                            break;
                        case (DEL_VILLAGE):
                            delVillage(e);
                            break;
                        case (ADD_ROAD):
                            addRoad(e);
                            break;
                        case (DEL_ROAD):
                            delRoad(e);
                            break;
                        default:
                            throw new RuntimeException("Uhhhhhh");
                    }
                }
            }
            repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (state != ADD_VILLAGE)
                return;
            String name = (String) JOptionPane.showInputDialog(mapGUI, "What would you like to name the village?");
            int id = map.addVillage(name);
            map.getVillage(id).setX(e.getX());
            map.getVillage(id).setY(e.getY());
            mapPanel.refreshVillages();
            state = NEUTRAL;
            repaint();
        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyChar()) {
                case ('q'):
                    state = ADD_VILLAGE;
                    prev = null;
                    break;
                case ('w'):
                    state = DEL_VILLAGE;
                    prev = null;
                    break;
                case ('e'):
                    state = ADD_ROAD;
                    prev = null;
                    break;
                case ('r'):
                    state = DEL_ROAD;
                    prev = null;
                    break;
                default:
                    state = NEUTRAL;
                    prev = null;
            }
        }

        /* UNUSED METHODS */
        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

    }

    private <T> LinkedList<T> convertMyList(MyList<T> list) {
        LinkedList<T> out = new LinkedList<T>();
        for (int i = 0; i < list.getSize(); i++) {
            T elem = list.get(i);
            if (elem != null) {
                out.add(elem);
            }
        }
        return out;
    }

    public static void main(String[] args) {
        Map map = new Map();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MapGUI(map);
            }
        });
    }
}