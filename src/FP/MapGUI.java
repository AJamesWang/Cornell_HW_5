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
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;

import datastructures.LinkedList;
import datastructures.Tuple;

public class MapGUI extends JFrame {
    private static int ROAD_ARROW_RATIO = 10;
    MapGUI mapGUI;
    Map map;
    MapPanel mapPanel;
    MapButtonPanel buttonPanel;
    MapListener mapListener;

    public MapGUI(Map map) {
        super("Main Frame");
        RoadTrip.setMapGUI(this);
        this.mapGUI = this;
        this.map = map;
        this.mapListener = new MapListener();
        addMouseListener(mapListener);
        addKeyListener(mapListener);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        this.mapPanel = new MapPanel();
        this.add(mapPanel, BorderLayout.CENTER);

        this.buttonPanel = new MapButtonPanel();
        this.add(buttonPanel, BorderLayout.SOUTH);

        this.pack();
        this.setVisible(true);
    }

    public void refresh(){
        mapPanel.refreshVillages();
    }

    class MapPanel extends JPanel {
        // button and associated village
        LinkedList<Tuple<JButton, Village>> villages;
        HashMap<Village, LinkedList<Gnome>> villageGnomes;
        HashMap<Village, LinkedList<Gnome>> emmigratingGnomes;
        HashMap<Village, LinkedList<Gnome>> immigratingGnomes;

        public MapPanel() {
            super();
            setLayout(null);
            this.villages = new LinkedList<Tuple<JButton, Village>>();
            addKeyListener(mapListener);
            refreshVillages();
        }

        private void refreshVillages() {
            for (int i = 0; i < villages.getLength(); i++) {
                JButton button = villages.get(i).getA();
                button.getParent().remove(button);
            }
            villages = new LinkedList<Tuple<JButton, Village>>();
            makeVillages();
            updateGnomes();
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
            out.addActionListener(mapListener);
            out.addKeyListener(mapListener);
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

        protected void updateGnomes() {
            MyList<Gnome> gnomes = map.getGnomes();
            villageGnomes = new HashMap<Village, LinkedList<Gnome>>();
            immigratingGnomes = new HashMap<Village, LinkedList<Gnome>>();
            emmigratingGnomes = new HashMap<Village, LinkedList<Gnome>>();
            // fills maps w/ empty lists tagged w/ every road
            for (int i = 0; i < villages.getLength(); i++) {
                villageGnomes.put(villages.get(i).getB(), new LinkedList<Gnome>());
                immigratingGnomes.put(villages.get(i).getB(), new LinkedList<Gnome>());
                emmigratingGnomes.put(villages.get(i).getB(), new LinkedList<Gnome>());
            }

            // tags every gnome w/ village or road, puts in respective map
            for (int i = 0; i < gnomes.getSize(); i++) {
                Gnome gnome = gnomes.get(i);
                if (gnome == null)
                    continue;

                if (gnome.getCurrentVillage() != null) {
                    Village village = gnome.getCurrentVillage();
                    villageGnomes.get(village).add(gnome);
                } else if (gnome.getCurrentRoad() != null) {
                    Road road = gnome.getCurrentRoad();
                    immigratingGnomes.get(map.getVillage(road.getToID())).add(gnome);
                    emmigratingGnomes.get(map.getVillage(road.getFromID())).add(gnome);
                }
            }
        }

        @Override
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

        private void paintGnomes(Graphics g) {
            for (Village village : villageGnomes.keySet()) {
                LinkedList<Gnome> gnomes = villageGnomes.get(village);
                int gnomeCount = gnomes.getLength();
                if (gnomeCount > 0) {
                    // max size=6, min size=2
                    int size = Math.max(Gnome.MIN_SIZE, Math.min(Gnome.MAX_SIZE, Village.DIAMETER / gnomeCount));
                    int x = village.getX();
                    int y = village.getY() + Village.DIAMETER;
                    for (int i = 0; i < gnomeCount; i++) {
                        g.setColor(gnomes.get(i).getFavColor());
                        g.fillRect(x, y, size, size);
                        x += size+1;
                    }
                }
            }
        }

        @Override
        // not in paintComponent b/c I want to paint over the buttons
        protected void paintChildren(Graphics g) {
            super.paintChildren(g);
            paintRoads(g);
            paintGnomes(g);
        }

        public Village getVillage(JButton button) {
            Tuple<JButton, Village> shell = new Tuple<JButton, Village>(button, null);
            return villages.get(villages.getIndex(shell)).getB();
        }
    }

    class MapButtonPanel extends JButton {
        JButton addVillage;
        JButton delVillage;
        JButton addRoad;
        JButton delRoad;

        public MapButtonPanel() {
            super();
            setLayout(new GridLayout(0, 4));

            addVillage = new JButton("Add Village (q)");
            addVillage.addActionListener(mapListener);
            add(addVillage);

            delVillage = new JButton("Delete Village (w)");
            delVillage.addActionListener(mapListener);
            add(delVillage);

            addRoad = new JButton("Add Road (e)");
            addRoad.addActionListener(mapListener);
            add(addRoad);

            delRoad = new JButton("Delete Road (r)");
            delRoad.addActionListener(mapListener);
            add(delRoad);

            addKeyListener(mapListener);
        }
    }

    class VillageInfoPanel extends JPanel {
        Village village;
        InfoPanel info;
        GnomeCreationPanel gnomeCreation;
        VillageListener villageListener;

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

        public VillageInfoPanel(ActionEvent e) {
            villageListener = new VillageListener();

            this.setLayout(new BorderLayout());

            //no longer needed b/c it's being opened in a JOptionPane
            // this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            village = mapPanel.getVillage((JButton) e.getSource());
            info = new InfoPanel(village);
            this.add(info, BorderLayout.CENTER);

            gnomeCreation = new GnomeCreationPanel();
            this.add(gnomeCreation, BorderLayout.SOUTH);

            this.setSize(300, 400);
            this.setLocation(mapGUI.getX() + village.getX(), mapGUI.getY() + village.getY());
            this.setVisible(true);
        }

        // @todo: refresh infopanel whenever new gnome is added
        class InfoPanel extends JPanel {
            public InfoPanel(Village village) {

                String name = village.getName();
                int id = village.getID();
                LinkedList<Road> roadsIn = convertMyList(village.getRoadsIn());
                LinkedList<Village> in = extractVillages(roadsIn, true);
                LinkedList<Road> roadsOut = convertMyList(village.getRoadsOut());
                LinkedList<Village> out = extractVillages(roadsOut, false);
                LinkedList<Gnome> gnomes = mapPanel.villageGnomes.get(village);

                GridLayout layout = new GridLayout(0, 1);
                layout.setVgap(0);
                this.setLayout(layout);
                this.setBorder(new EmptyBorder(10, 10, 10, 10));

                JLabel nameLabel = new JLabel("Village: " + name);
                this.add(nameLabel);
                JLabel idLabel = new JLabel("ID: " + id);
                this.add(idLabel);

                this.add(new JLabel());

                JLabel roadsInTitle = new JLabel("Inbound roads from: ");
                this.add(roadsInTitle);
                JLabel roadsInData = new JLabel("...." + in.toString());
                this.add(roadsInData);

                this.add(new JLabel());

                JLabel roadsOutTitle = new JLabel("Outbound roads to: ");
                this.add(roadsOutTitle);
                JLabel roadsOutData = new JLabel("...." + out.toString());
                this.add(roadsOutData);

                this.add(new JLabel());

                JLabel gnomesTitle = new JLabel("Gnomes residing in city: " + gnomes.getLength());
                this.add(gnomesTitle);
                JLabel gnomesData = new JLabel("...." + gnomes.toString());
                this.add(gnomesData);

                // @todo: expand window if there are many connected roads
                this.setVisible(true);

            }
        }

        class GnomeCreationPanel extends JPanel {
            JButton addGnome;
            JButton delGnome;
            JButton inspectGnome;

            public GnomeCreationPanel() {
                this.setLayout(new GridLayout(0, 1));

                addGnome = new JButton("Birth a gnome in " + village.getName());
                addGnome.addActionListener(villageListener);
                this.add(addGnome);

                delGnome = new JButton("Execute a gnome");
                delGnome.addActionListener(villageListener);
                this.add(delGnome);

                inspectGnome = new JButton("Inspect a particular gnome");
                inspectGnome.addActionListener(villageListener);
                this.add(inspectGnome);
            }
        }

        class VillageListener implements ActionListener {

            // name, colour, VIP level, ID
            private Object[] promptGnomeInfo(Gnome gnome) {
                if (gnome == null) {
                    gnome = new Gnome("", null, 0, -1);
                }

                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(0, 2));

                JTextField name = new JTextField(gnome.getName());
                {
                    JLabel nameLabel = new JLabel("Name: ");

                    panel.add(nameLabel);
                    panel.add(name);
                }

                JButton color = new JButton();
                {
                    // @todo: make this a list?
                    JLabel colorLabel = new JLabel("Favorite Color: ");
                    panel.add(colorLabel);

                    color.setOpaque(true);
                    color.setBorderPainted(false);
                    color.setBackground(gnome.getFavColor() == null ? Color.blue : gnome.getFavColor());
                    color.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // @todo: remove all the "sample text" from colorChooser
                            Color newColor = JColorChooser.showDialog(color, "Favorite Color", color.getBackground());
                            color.setBackground(newColor);
                        }

                    });
                    panel.add(color);

                }

                JSpinner vip = new JSpinner();
                {
                    JLabel vipLabel = new JLabel("VIP status: ");
                    panel.add(vipLabel);

                    SpinnerNumberModel vipModel = new SpinnerNumberModel();
                    vipModel.setMinimum(0);
                    vipModel.setValue(gnome.getVIPLevel());
                    vip.setModel(vipModel);
                    panel.add(vip);
                }

                JLabel id = new JLabel(gnome.getID() + "");
                if (gnome.getID() != -1) {
                    JLabel idLabel = new JLabel("ID: ");
                    panel.add(idLabel);

                    //do not want user to be able to edit id
                    // SpinnerNumberModel idModel = new SpinnerNumberModel();
                    // idModel.setMinimum(0);
                    // idModel.setValue(gnome.getID());
                    // id.setModel(idModel);
                    panel.add(id);
                }

                JButton roadTripInfo=new JButton("Current road trip status");
                if (gnome.getID()!=-1){
                    final Gnome gnomeFinal=gnome;
                    roadTripInfo.addActionListener(new ActionListener(){
                        @Override
                        public void actionPerformed(ActionEvent e){
                            RoadTrip roadTrip=gnomeFinal.getCurRoadTrip();
                            if(roadTrip==null || !roadTrip.isAlive()){
                                JOptionPane.showMessageDialog(mapGUI, gnomeFinal.getName()+" is currently not on a trip", "Road Trip Info", JOptionPane.PLAIN_MESSAGE);
                            } else{
                                String name=gnomeFinal.getName();
                                String speed=roadTrip.getMode()==RoadTrip.LAZY_MODE?"scenic":"direct";
                                Village destination = roadTrip.getDestination();
                                Village roadFrom = map.getVillage(gnomeFinal.getCurrentRoad().getToID());
                                Village roadTo = map.getVillage(gnomeFinal.getCurrentRoad().getFromID());
                                double progress = roadTrip.getProgress();
                                long timeTraveled=roadTrip.getTravelTime();

                                String line1=gnomeFinal.getName()+" is on a "+speed+" trip to "+destination.getName()+".\n";
                                String line2="This gnome is currenly on the road connecting "+roadFrom.getName()+" and "+roadTo.getName()+".\n";
                                String line3="It (he? she? they? Alas, gnome gender may forever remain a mystery) is currently "+progress+
                                             " of the way there,\n having traveled for "+timeTraveled+" days.";
                                JOptionPane.showMessageDialog(mapGUI, line1+line2+line3, "Road Trip Info", JOptionPane.PLAIN_MESSAGE);
                            }
                        }
                    });
                    panel.add(roadTripInfo);
                }

                //@todo: make roadtrip only start once optionpane is closed
                JButton roadTripNew=new JButton("Go on a road trip!");
                if (gnome.getID()!=-1){
                    final Gnome gnomeFinal=gnome;
                    roadTripNew.addActionListener(new ActionListener(){
                        @Override
                        public void actionPerformed(ActionEvent e){
                            RoadTrip roadTripOld=gnomeFinal.getCurRoadTrip();

                            JPanel villagesPanel=new JPanel();
                            JLabel villagesLabel=new JLabel("Select village: ");
                            villagesPanel.add(villagesLabel);
                            Object[] selectionValues=convertMyList(map.getVillages()).toArray();
                            JComboBox villages=new JComboBox(selectionValues);
                            villagesPanel.add(villages);

                            Object[] buttonLabels={"Scenic", "Direct", "Cancel"};
                            int selection=JOptionPane.showOptionDialog(mapGUI, villagesPanel, "Road Trip Planning!"
                                          ,JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttonLabels, JOptionPane.YES_OPTION);

                            RoadTrip roadTripNew=null;
                            if(selection==JOptionPane.YES_OPTION){
                                gnomeFinal.setNewRoadTrip(map, (Village)villages.getSelectedItem(),RoadTrip.LAZY_MODE);
                                if(roadTripOld!=null)
                                    roadTripOld.interrupt();
                                gnomeFinal.getCurRoadTrip().start();
                            } else if(selection==JOptionPane.NO_OPTION){
                                gnomeFinal.setNewRoadTrip(map, (Village)villages.getSelectedItem(),RoadTrip.EFFICIENT_MODE);
                                if(roadTripOld!=null)
                                    roadTripOld.interrupt();
                                gnomeFinal.getCurRoadTrip().start();
                            } else{
                                roadTripNew.interrupt();
                            }
                        }
                    });
                    panel.add(roadTripNew);
                }

                int result = JOptionPane.showConfirmDialog(mapGUI, panel, "Gnomes", JOptionPane.YES_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    Object[] out = new Object[3];
                    out[0] = name.getText();
                    out[1] = color.getBackground();
                    out[2] = vip.getValue();

                    return out;
                } else {
                    return null;
                }
            }

            private void addGnome(ActionEvent e) {
                Object[] data = promptGnomeInfo(null);
                if (data == null)
                    return;

                int id = map.addGnome((String) data[0], (Color) data[1], (Integer) data[2]);
                map.getGnome(id).setInVillage(village);
                refresh();
            }

            private void delGnome(ActionEvent e) {
                int id = Integer.parseInt(JOptionPane.showInputDialog(mapGUI, "Gnome ID?", "Gnome Deletion",
                                JOptionPane.QUESTION_MESSAGE));
                map.getGnomes().set_null(id);
                refresh();
            }

            private void inspectGnome(ActionEvent e) {
                int id = Integer.parseInt(JOptionPane.showInputDialog(mapGUI, "Gnome ID?", "Gnome Deletion",
                                JOptionPane.QUESTION_MESSAGE));
                Gnome gnome = map.getGnome(id);
                if (gnome != null) {
                    Object[] info = promptGnomeInfo(gnome);
                    gnome.setName((String) info[0]);
                    gnome.setFavColor((Color) info[1]);
                    gnome.setVIPLevel((Integer) info[2]);
                }
                refresh();
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource().getClass().equals(new JButton().getClass())) {
                    if (e.getSource().equals(gnomeCreation.addGnome)) {
                        addGnome(e);
                    } else if (e.getSource().equals(gnomeCreation.delGnome)) {
                        delGnome(e);
                    } else if (e.getSource().equals(gnomeCreation.inspectGnome)) {
                        inspectGnome(e);
                    } else {
                        throw new RuntimeException("Uhhhhh");
                    }
                } else {
                    throw new RuntimeException("uhhhh");
                }
            }

        }

    }

    class MapListener implements ActionListener, MouseListener, KeyListener {
        private static final int NEUTRAL = 0;
        private static final int ADD_VILLAGE = 11;
        private static final int DEL_VILLAGE = 21;
        private static final int ADD_ROAD = 12;
        private static final int DEL_ROAD = 22;

        int state;
        Village prev;

        private boolean roadInfoShown;

        public MapListener() {
            this.state = NEUTRAL;
            this.prev = null;

            roadInfoShown = false;
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
            refresh();
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
                    int weight = Integer.parseInt(JOptionPane.showInputDialog(mapGUI, "How much is the toll?"));
                    map.addRoad(prev.getID(), next.getID(), weight);
                }
                prev = null;
                state = NEUTRAL;
            }
            refresh();
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
            refresh();
        }

        private void showVillageInfo(ActionEvent e) {
            JOptionPane.showMessageDialog(mapGUI, new VillageInfoPanel(e), "Village Information",
                            JOptionPane.PLAIN_MESSAGE);
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
            if (!roadInfoShown && state == DEL_ROAD) {
                JOptionPane.showMessageDialog(mapGUI,
                                "To delete a road:\nFirst select the village it is leaving.\nThen, select the village it is heading towards.",
                                "Foolish Mortal", JOptionPane.PLAIN_MESSAGE);
                roadInfoShown = true;
                return;
            } else if (state != ADD_VILLAGE)
                return;
            String name = JOptionPane.showInputDialog(mapGUI, "What would you like to name the village?");
            int id = map.addVillage(name);
            // don't know why coors have inconsistent offsets
            // so village stays centered(ish) on mouse
            map.getVillage(id).setX(e.getX() - Village.DIAMETER / 2);
            map.getVillage(id).setY(e.getY() - Village.DIAMETER);
            refresh();
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
            @Override
            public void run() {
                new MapGUI(map);
            }
        });
    }
}