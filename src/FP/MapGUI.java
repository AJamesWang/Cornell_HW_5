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
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.Font;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.util.HashMap;
import java.util.HashSet;

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
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;

import datastructures.LinkedList;
import datastructures.Tuple;

/*@todo:

  incorporate BST into GUI
  make pause button
  let gnomes move randomly or shift to adjacent village
  make road toll/capacity clearer
  limit color choices to clearly distinguishable colours
*/

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
        // @todo: refactor witih DataButton
        LinkedList<Tuple<JButton, Village>> villages;
        HashMap<Village, LinkedList<Gnome>> villageGnomes;
        HashMap<Road, LinkedList<Gnome>> roadGnomes;
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
            roadGnomes = new HashMap<Road, LinkedList<Gnome>>();
            // v these are currently unused
            immigratingGnomes = new HashMap<Village, LinkedList<Gnome>>();
            emmigratingGnomes = new HashMap<Village, LinkedList<Gnome>>();
            // fills maps w/ empty lists tagged w/ every road
            for (int i = 0; i < villages.getLength(); i++) {
                villageGnomes.put(villages.get(i).getB(), new LinkedList<Gnome>());
                immigratingGnomes.put(villages.get(i).getB(), new LinkedList<Gnome>());
                emmigratingGnomes.put(villages.get(i).getB(), new LinkedList<Gnome>());
            }

            for(int i=0 ; i < map.getRoads().getSize(); i++){
                if(map.getRoads().get(i)!=null){
                    roadGnomes.put(map.getRoads().get(i), new LinkedList<Gnome>());
                }
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
                    roadGnomes.get(road).add(gnome);
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

            if(mapListener.proposedRoads!=null){
                for(Object obj:mapListener.proposedRoads.toArray()){
                    Road road=(Road)obj;

                    if (road != null) {
                        int x1 = map.getVillage(road.getFromID()).getX() + Village.DIAMETER / 2;
                        int y1 = map.getVillage(road.getFromID()).getY() + Village.DIAMETER / 2;
                        int x2 = map.getVillage(road.getToID()).getX() + Village.DIAMETER / 2;
                        int y2 = map.getVillage(road.getToID()).getY() + Village.DIAMETER / 2;

                        int x3 = x2 - ((x2 - x1) / ROAD_ARROW_RATIO);
                        int y3 = y2 - ((y2 - y1) / ROAD_ARROW_RATIO);
                        arrows.add(new Tuple(road.getWeight(), new Tuple(new Tuple(x3, y3), new Tuple(x2, y2))));

                        ((Graphics2D) g).setStroke(new BasicStroke(road.getWeight(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{10.0f}, 0.0f));
                        g.setColor(Color.green);
                        g.drawLine(x1, y1, x2, y2);
                    }
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

            for (Road road : roadGnomes.keySet()){
                LinkedList<Gnome> gnomes = roadGnomes.get(road);
                int gnomeCount = gnomes.getLength();
                if(gnomeCount > 0) {
                    Village from=map.getVillage(road.getFromID());
                    Village to=map.getVillage(road.getToID());

                    int size = Gnome.MAX_SIZE*2;
                    int x = (to.getX()+from.getX())/2;
                    int y = (to.getY()+from.getY())/2;
                    int dy = (to.getY()-from.getY()) * (size+1)/(to.getX()-from.getX());
                    for (int i = 0; i < gnomeCount; i++) {
                        g.setColor(gnomes.get(i).getFavColor());
                        g.fillOval(x, y, size, size);
                        x += size+1;
                        y += dy;
                    }

                }
            }
        }

        private void paintInfo(Graphics g){
            g.setColor(Color.black);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
            switch(mapListener.state){
                case(MapListener.NEUTRAL):
                    g.drawString("VIEWING:", 5, 20);
                    break;
                case(MapListener.ADD_VILLAGE):
                    g.drawString("ADD VILLAGE:", 5, 20);
                    break;
                case(MapListener.DEL_VILLAGE):
                    g.drawString("DELETE VILLAGE:", 5, 20);
                    break;
                case(MapListener.ADD_ROAD):
                    g.drawString("ADD ROAD:", 5, 20);
                    g.drawString("From: "+((mapListener.prev==null)? "":mapListener.prev.toString())+" To:", 5, 20+g.getFontMetrics().getHeight());
                    break;
                case(MapListener.DEL_ROAD):
                    g.drawString("DELETE ROAD:", 5, 20);
                    g.drawString("From: "+((mapListener.prev==null)? "":mapListener.prev.toString())+" To:", 5, 20+g.getFontMetrics().getHeight());
                    break;
                case(MapListener.PROPOSE_ROADS):
                    g.drawString("PROPOSE ROADS:", 5, 20);
                    g.drawString("From: "+((mapListener.prev==null)? "":mapListener.prev.toString())+" To:", 5, 20+g.getFontMetrics().getHeight());
                    break;
                default:
                    throw new RuntimeException("Uhhhhh");
            }


            g.setColor(Color.black);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
            for(Object obj:villages.toArray()){
                Village village=((Tuple<JButton, Village>) obj).getB();

                int id=village.getID();
                int x=village.getX();
                int y=village.getY();
                g.drawString("ID: "+id, x+5, y+Village.DIAMETER/2);
            }
        }

        @Override
        // not in paintComponent b/c I want to paint over the buttons
        protected void paintChildren(Graphics g) {
            super.paintChildren(g);
            paintRoads(g);
            paintGnomes(g);
            paintInfo(g);
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
        JButton viewGnomes;
        JButton proposeRoads;

        public MapButtonPanel() {
            super();
            setLayout(new GridLayout(0, 4));

            addVillage = new JButton("Add Village (q)");
            addVillage.addActionListener(mapListener);
            addVillage.addKeyListener(mapListener);
            add(addVillage);

            delVillage = new JButton("Delete Village (w)");
            delVillage.addActionListener(mapListener);
            delVillage.addKeyListener(mapListener);
            add(delVillage);

            addRoad = new JButton("Add Road (e)");
            addRoad.addActionListener(mapListener);
            addRoad.addKeyListener(mapListener);
            add(addRoad);

            delRoad = new JButton("Delete Road (r)");
            delRoad.addActionListener(mapListener);
            delRoad.addKeyListener(mapListener);
            add(delRoad);

            viewGnomes = new JButton("Gnomes");
            viewGnomes.addActionListener(mapListener);
            viewGnomes.addKeyListener(mapListener);
            add(viewGnomes);

            proposeRoads=new JButton("Propose New Roads");
            proposeRoads.addActionListener(mapListener);
            proposeRoads.addKeyListener(mapListener);
            add(proposeRoads);

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

        //ugly, I know. Allows GnomeFrame to reuse villageListener.promptGnomeInfo();
        public VillageInfoPanel(){
            villageListener=new VillageListener();
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
                LinkedList<Road> in = convertMyList(village.getRoadsIn());
                // LinkedList<Village> in = extractVillages(roadsIn, true);
                LinkedList<Road> out = convertMyList(village.getRoadsOut());
                // LinkedList<Village> out = extractVillages(roadsOut, false);
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

                JLabel gnomesTitle = new JLabel("Gnomes residing in city: " + gnomes.getLength() + "/"+village.getCapacity());
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

            // name, colour, VIP level
            public Object[] promptGnomeInfo(Gnome gnome) {
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
                            if(color!=null){
                                color.setBackground(newColor);
                            }
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
                                Village roadFrom = map.getVillage(gnomeFinal.getCurrentRoad().getToID());
                                Village roadTo = map.getVillage(gnomeFinal.getCurrentRoad().getFromID());
                                long timeTraveled=roadTrip.getTravelTime();

                                if(roadTrip.getMode()==RoadTrip.WANDER_MODE){
                                    JOptionPane.showMessageDialog(mapGUI, name + " is currently wandering, and has traveled for " + timeTraveled + " days");
                                } else{
                                    String speed=roadTrip.getMode()==RoadTrip.LAZY_MODE?"scenic":"direct";
                                    Village destination = roadTrip.getDestination();
                                    double progress = roadTrip.getProgress();

                                    String line1=name+" is on a "+speed+" trip to "+destination.getName()+".\n";
                                    String line2="This gnome is currenly on the road connecting "+roadFrom.getName()+" and "+roadTo.getName()+".\n";
                                    String line3="It (he? she? they? Alas, gnome gender may forever remain a mystery) is currently "+progress+
                                                 " of the way there,\n having traveled for "+timeTraveled+" days.";
                                    JOptionPane.showMessageDialog(mapGUI, line1+line2+line3, "Road Trip Info", JOptionPane.PLAIN_MESSAGE);
                                }
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

                            Object[] buttonLabels={"Scenic", "Direct", "Wandering", "Cancel"};
                            int selection=JOptionPane.showOptionDialog(mapGUI, villagesPanel, "Road Trip Planning!"
                                          ,JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttonLabels, JOptionPane.YES_OPTION);

                            RoadTrip roadTripNew=null;
                            if(selection==0){
                                gnomeFinal.setNewRoadTrip(map, (Village)villages.getSelectedItem(),RoadTrip.LAZY_MODE);
                                if(roadTripOld!=null)
                                    roadTripOld.interrupt();
                                gnomeFinal.getCurRoadTrip().start();
                            } else if(selection==1){
                                gnomeFinal.setNewRoadTrip(map, (Village)villages.getSelectedItem(),RoadTrip.EFFICIENT_MODE);
                                if(roadTripOld!=null)
                                    roadTripOld.interrupt();
                                gnomeFinal.getCurRoadTrip().start();
                            } else if(selection==2){
                                gnomeFinal.setNewRoadTrip(map, (Village)villages.getSelectedItem(),RoadTrip.WANDER_MODE);
                                if(roadTripOld!=null)
                                    roadTripOld.interrupt();
                                gnomeFinal.getCurRoadTrip().start();
                            } else{
                                // roadTripNew.interrupt();
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
                String input=JOptionPane.showInputDialog(mapGUI, "Gnome ID?", "Gnome Deletion",
                                JOptionPane.QUESTION_MESSAGE);
                if(input!=null){
                    int id = Integer.parseInt(input);
                    if(id<map.getGnomes().getSize()){
                        map.getGnomes().set_null(id);
                    }
                }
                refresh();
            }

            private void inspectGnome(ActionEvent e) {
                String input=JOptionPane.showInputDialog(mapGUI, "Gnome ID?", "Gnome Inspection",
                                JOptionPane.QUESTION_MESSAGE);
                if(input!=null){
                    int id = Integer.parseInt(input);
                    if(id<map.getGnomes().getSize()){
                        Gnome gnome = map.getGnome(id);
                        if (gnome != null) {
                            Object[] info = promptGnomeInfo(gnome);
                            gnome.setName((String) info[0]);
                            gnome.setFavColor((Color) info[1]);
                            gnome.setVIPLevel((Integer) info[2]);
                        }
                    }
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

    class GnomeFrame extends JFrame {

        SearchPanel searchPanel;
        JPanel mainPanel;
        GnomeButtonListener gnomeButtonListener;

        public GnomeFrame(){
            this.setSize(500,500);
            this.setLayout(new BorderLayout());
            gnomeButtonListener=new GnomeButtonListener();

            searchPanel=new SearchPanel();
            this.add(searchPanel, BorderLayout.NORTH);
            refreshGnomeFrame();

            addKeyListener(mapListener);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.addWindowListener(new WindowListener(){
                @Override
                public void windowOpened(WindowEvent e){
                    mapListener.gnomesFrameIsActive=true;
                }

                @Override
                public void windowClosing(WindowEvent e){
                    mapListener.gnomesFrameIsActive=false;
                }

                @Override
                public void windowClosed(WindowEvent e){
                    mapListener.gnomesFrameIsActive=false;
                }

                @Override
                public void windowActivated(WindowEvent e){
                    refreshGnomeFrame();
                }

                @Override
                public void windowDeactivated(WindowEvent e){

                }

                @Override
                public void windowIconified(WindowEvent e){
                }

                @Override
                public void windowDeiconified(WindowEvent e){

                }
            });

            this.addKeyListener(mapListener);
            this.setVisible(true);
        }

        private void refreshGnomeFrame(){
            int idMin=(Integer)searchPanel.idMin.getValue();
            int idMax=(Integer)searchPanel.idMax.getValue();
            String _name=searchPanel.name.getText();
            Color _color=searchPanel.color.getData();
            int statusMin=(Integer)searchPanel.statusMin.getValue();
            int statusMax=(Integer)searchPanel.statusMax.getValue();
            int _village=(Integer)searchPanel.village.getValue();


            Gnome.NumberRange id=new Gnome.NumberRange(idMin<1?null:idMin, idMax<1?null:idMax);
            Gnome.StringRange name=new Gnome.StringRange(_name);
            Gnome.ColorRange color=new Gnome.ColorRange(_color);
            Gnome.NumberRange status=new Gnome.NumberRange(statusMin<1?null:statusMin, statusMax<1?null:statusMax);
            Gnome.NumberRange village=new Gnome.NumberRange(_village<1?null:_village, _village<1?null:_village);

            if(mainPanel!=null){
                this.getContentPane().remove(mainPanel);
                this.validate();
            }
            GnomeInfoPanel gnomeInfoPanel=new GnomeInfoPanel(id, name, color, status, village);
            JScrollPane scrollPane=new JScrollPane(gnomeInfoPanel);
            mainPanel=new JPanel();
            mainPanel.add(scrollPane);
            this.getContentPane().add(mainPanel, BorderLayout.CENTER);
            this.validate();
            repaint();
        }


        class SearchPanel extends JPanel{
            JSpinner idMin;
            JSpinner idMax;
            JTextField name;
            DataButton<Color> color;
            JSpinner statusMin;
            JSpinner statusMax;
            JSpinner village;

            public SearchPanel(){
                this.setLayout(new GridLayout(2, 6));

                add(new JLabel("ID min"));
                add(new JLabel("ID max"));
                add(new JLabel("Name Filter"));
                add(new JLabel("Color Filter"));
                add(new JLabel("Min Status"));
                add(new JLabel("Max Status"));
                add(new JLabel("Village ID"));

                idMin=new JSpinner();
                SpinnerNumberModel idMinModel=new SpinnerNumberModel();
                idMinModel.setMinimum(-1);
                idMinModel.setValue(-1);
                idMin.setModel(idMinModel);
                idMin.addChangeListener(new ChangeListener(){
                    @Override
                    public void stateChanged(ChangeEvent e){
                        refreshGnomeFrame();
                    }
                });
                add(idMin);

                idMax=new JSpinner();
                SpinnerNumberModel idMaxModel=new SpinnerNumberModel();
                idMaxModel.setMinimum(-1);
                idMaxModel.setValue(-1);
                idMax.setModel(idMaxModel);
                idMax.addChangeListener(new ChangeListener(){
                    @Override
                    public void stateChanged(ChangeEvent e){
                        refreshGnomeFrame();
                    }
                });
                add(idMax);

                name=new JTextField();
                name.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e){
                        refreshGnomeFrame();
                    }
                });
                add(name);

                color=new DataButton<Color>();
                color.setOpaque(true);
                color.setBorderPainted(false);
                color.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e){
                        Color colorData=JColorChooser.showDialog(color, "Favorite Color", color.getData());
                        color.setData(colorData);
                        if(color!=null){
                            color.setBackground(colorData);
                        }
                        refreshGnomeFrame();
                    }
                });
                add(color);

                statusMin=new JSpinner();
                SpinnerNumberModel statusMinModel=new SpinnerNumberModel();
                statusMinModel.setMinimum(-1);
                statusMinModel.setValue(-1);
                statusMin.setModel(statusMinModel);
                statusMin.addChangeListener(new ChangeListener(){
                    @Override
                    public void stateChanged(ChangeEvent e){
                        refreshGnomeFrame();
                    }
                });
                add(statusMin);


                statusMax=new JSpinner();
                SpinnerNumberModel statusMaxModel=new SpinnerNumberModel();
                statusMaxModel.setMinimum(-1);
                statusMaxModel.setValue(-1);
                statusMax.setModel(statusMaxModel);
                statusMax.addChangeListener(new ChangeListener(){
                    @Override
                    public void stateChanged(ChangeEvent e){
                        refreshGnomeFrame();
                    }
                });
                add(statusMax);

                village=new JSpinner();
                SpinnerNumberModel villageModel=new SpinnerNumberModel();
                villageModel.setMinimum(-1);
                villageModel.setValue(-1);
                village.setModel(villageModel);
                village.addChangeListener(new ChangeListener(){
                    @Override
                    public void stateChanged(ChangeEvent e){
                        refreshGnomeFrame();
                    }
                });
                add(village);

                addKeyListener(mapListener);
            }
        }

        class GnomeInfoPanel extends JPanel{

            //@todo:implement strictness
            //if strict: follows all restrictions         if not strict: follows at least 1 restriction
            public GnomeInfoPanel(Gnome.NumberRange idRange, Gnome.StringRange nameRange, Gnome.ColorRange colorRange
                                  , Gnome.NumberRange statusRange, Gnome.NumberRange villageRange){
                //id, name, color, VIP
                setLayout(new GridLayout(0, 5));

                JLabel id=new JLabel("ID");
                id.setBorder(BorderFactory.createLineBorder(Color.black));
                JLabel name=new JLabel("Name");
                name.setBorder(BorderFactory.createLineBorder(Color.black));
                JLabel color=new JLabel("Favorite Color");
                color.setBorder(BorderFactory.createLineBorder(Color.black));
                JLabel status=new JLabel("VIP Status");
                status.setBorder(BorderFactory.createLineBorder(Color.black));
                JLabel village=new JLabel("Location");
                village.setBorder(BorderFactory.createLineBorder(Color.black));


                add(id);
                add(name);
                add(color);
                add(status);
                add(village);

                LinkedList<Gnome> gnomes=convertMyList(map.getGnomes());
                for(Object obj:gnomes.toArray()){
                    Gnome gnome=(Gnome)obj;
                    this.addGnome(gnome, idRange, nameRange, colorRange, statusRange, villageRange);
                }

                addKeyListener(mapListener);
            }

            private void addGnome(Gnome gnome, Gnome.NumberRange idRange, Gnome.StringRange nameRange, Gnome.ColorRange colorRange
                                , Gnome.NumberRange statusRange, Gnome.NumberRange villageRange){
                int _id=gnome.getID();
                String _name=gnome.getName();
                Color _color=gnome.getFavColor();
                int _status=gnome.getVIPLevel();
                int _village;
                if(gnome.getCurrentVillage()!=null){
                    _village=gnome.getCurrentVillage().getID();
                } else{
                    _village=-1;
                }
                if(idRange.contains(_id) && nameRange.contains(_name) 
                   && colorRange.contains(_color) && statusRange.contains(_status) && villageRange.contains(_village)){

                    DataButton<Gnome> id=new DataButton<Gnome>(_id+"");
                    id.setBorder(BorderFactory.createLineBorder(Color.black));
                    id.setData(gnome);
                    id.addActionListener(gnomeButtonListener);
                    id.addKeyListener(mapListener);

                    DataButton<Gnome> name=new DataButton<Gnome>(_name);
                    name.setBorder(BorderFactory.createLineBorder(Color.black));
                    name.setData(gnome);
                    name.addActionListener(gnomeButtonListener);
                    name.addKeyListener(mapListener);

                    DataButton<Gnome> color=new DataButton<Gnome>();
                    color.setBackground(_color);
                    color.setOpaque(true);
                    color.setBorder(BorderFactory.createLineBorder(Color.black));
                    color.setData(gnome);
                    color.addActionListener(gnomeButtonListener);
                    color.addKeyListener(mapListener);

                    DataButton<Gnome> status=new DataButton<Gnome>(_status+"");
                    status.setBorder(BorderFactory.createLineBorder(Color.black));
                    status.setData(gnome);
                    status.addActionListener(gnomeButtonListener);
                    status.addKeyListener(mapListener);

                    DataButton<Gnome> village;
                    if(_village!=-1){
                        village=new DataButton<Gnome>(_village+"");
                    } else{
                        village=new DataButton<Gnome>("On Road");
                    }
                    village.setBorder(BorderFactory.createLineBorder(Color.black));
                    village.setData(gnome);
                    village.addActionListener(gnomeButtonListener);
                    village.addKeyListener(mapListener);

                    add(id);
                    add(name);
                    add(color);
                    add(status);
                    add(village);
                }
            }
        }

        class GnomeButtonListener implements ActionListener{
            VillageInfoPanel vip=new VillageInfoPanel();
            @Override
            public void actionPerformed(ActionEvent e){
                DataButton<Gnome> db=(DataButton<Gnome>) e.getSource();
                // name, colour, VIP level
                Gnome gnome=db.getData();
                Object[] info=vip.villageListener.promptGnomeInfo(db.getData());
                if(info!=null){
                    gnome.setName((String)info[0]);
                    gnome.setFavColor((Color)info[1]);
                    gnome.setVIPLevel((Integer)info[2]);
                }
                refreshGnomeFrame();
            }
        }
    }

    class MapListener implements ActionListener, MouseListener, KeyListener {
        private static final int NEUTRAL = 0;
        private static final int ADD_VILLAGE = 11;
        private static final int DEL_VILLAGE = 21;
        private static final int ADD_ROAD = 12;
        private static final int DEL_ROAD = 22;
        private static final int PROPOSE_ROADS = 32;

        private int state;
        private Village prev;
        private boolean gnomesFrameIsActive;
        private boolean roadInfoShown;
        private LinkedList<Road> proposedRoads;//@todo: decide where to put this

        public MapListener() {
            this.state = NEUTRAL;
            this.prev = null;

            gnomesFrameIsActive = false;
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
                    //allows for multiple roads connecting the same villages
                    JPanel roadPanel=new JPanel();
                    roadPanel.setLayout(new GridLayout(0,1));
                    JLabel tollLabel=new JLabel("Toll: ");
                    roadPanel.add(tollLabel);
                    JSpinner toll=new JSpinner();
                    SpinnerNumberModel tollModel = new SpinnerNumberModel();
                    tollModel.setMinimum(1);
                    tollModel.setMaximum(50);
                    tollModel.setValue(10);
                    toll.setModel(tollModel);
                    roadPanel.add(toll);
                    JLabel sizeLabel=new JLabel("Maximum Capacity: ");
                    roadPanel.add(sizeLabel);
                    JSpinner size=new JSpinner();
                    SpinnerNumberModel sizeModel = new SpinnerNumberModel();
                    sizeModel.setMinimum(1);
                    sizeModel.setValue(100);
                    size.setModel(sizeModel);
                    roadPanel.add(size);

                    int response=JOptionPane.showOptionDialog(mapGUI, roadPanel, "Road Creation"
                                                            ,JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{"OK", "Cancel"}, JOptionPane.YES_OPTION);
                    
                    if(response==JOptionPane.YES_OPTION){
                        map.addRoad(prev.getID(), next.getID(), (Integer) toll.getValue(), (Integer)size.getValue());
                    }
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
                map.removeRoad(prev.getID(), next.getID());
                prev = null;
                state = NEUTRAL;
            }
            refresh();
        }

        private void showVillageInfo(ActionEvent e) {
            JOptionPane.showMessageDialog(mapGUI, new VillageInfoPanel(e), "Village Information",
                            JOptionPane.PLAIN_MESSAGE);
        }

        private void viewGnomes(ActionEvent e){
            if(gnomesFrameIsActive)
                return;
            // moved to GnomeFrame
            // gnomesFrameIsActive=true;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new GnomeFrame();
                }
            });

        }

        private void proposeRoad(ActionEvent e){
            if (prev == null) {
                prev = mapPanel.getVillage((JButton) e.getSource());
            } else{
                Village next = mapPanel.getVillage((JButton) e.getSource());


                if (next == prev) {
                    JOptionPane.showMessageDialog(mapGUI, "That would be a pointless road", "Foolish Mortal",
                                    JOptionPane.WARNING_MESSAGE);
                } else {
                    //allows for multiple roads connecting the same villages
                    JPanel roadPanel=new JPanel();
                    roadPanel.setLayout(new GridLayout(0,1));
                    JLabel tollLabel=new JLabel("Toll: ");
                    roadPanel.add(tollLabel);
                    JSpinner toll=new JSpinner();
                    SpinnerNumberModel tollModel = new SpinnerNumberModel();
                    tollModel.setMinimum(1);
                    tollModel.setMaximum(50);
                    tollModel.setValue(10);
                    toll.setModel(tollModel);
                    roadPanel.add(toll);
                    JLabel sizeLabel=new JLabel("Maximum Capacity: ");
                    roadPanel.add(sizeLabel);
                    JSpinner size=new JSpinner();
                    SpinnerNumberModel sizeModel = new SpinnerNumberModel();
                    sizeModel.setMinimum(1);
                    sizeModel.setValue(100);
                    size.setModel(sizeModel);
                    roadPanel.add(size);

                    int response=JOptionPane.showOptionDialog(mapGUI, roadPanel, "Road Creation",JOptionPane.YES_NO_OPTION
                                                              , JOptionPane.PLAIN_MESSAGE, null, new Object[]{"OK", "Cancel"}, JOptionPane.YES_OPTION);
                    
                    if(response==JOptionPane.YES_OPTION){
                        proposedRoads.add(new Road(prev.getID(), next.getID(), (Integer) toll.getValue(), (Integer)size.getValue(), map.getNextRoadID()));
                    }
                }
                prev=null;
            }
        }

        private void evaluateRoads(){
            Object[] objs=proposedRoads.toArray();
            Road[] newRoads=new Road[objs.length];
            for(int i=0; i<objs.length; i++){
                newRoads[i]=(Road)objs[i];
            }

            HashSet<Road> acceptedRoads=map.chooseNewRoads(newRoads);
            // for(Road road:acceptedRoads){
            //     map.addRoad(road);
            // }
            proposedRoads=null;
            refresh();
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
                } else if (b.equals(buttonPanel.viewGnomes)){
                    viewGnomes(e);
                    state = NEUTRAL;
                    prev = null;
                } else if (b.equals(buttonPanel.proposeRoads)){
                    if(state!=PROPOSE_ROADS){
                        state = PROPOSE_ROADS;
                        proposedRoads=new LinkedList<Road>();
                        buttonPanel.proposeRoads.setText("Evaluate Roads");
                        prev=null;
                    } else{
                        evaluateRoads();
                        buttonPanel.proposeRoads.setText("Propose Roads");
                        state=NEUTRAL;
                        prev=null;
                    }
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
                        case (PROPOSE_ROADS):
                            proposeRoad(e);
                            break;
                        default:
                            throw new RuntimeException("Uhhhhhh");
                    }
                }
            }
            refresh();
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

            JPanel villagePanel=new JPanel();
            villagePanel.setLayout(new GridLayout(0,1));
            JLabel nameLabel=new JLabel("Name: ");
            villagePanel.add(nameLabel);
            JTextField name=new JTextField();
            villagePanel.add(name);
            JLabel sizeLabel=new JLabel("Maximum Capacity: ");
            villagePanel.add(sizeLabel);
            JSpinner size=new JSpinner();
            SpinnerNumberModel sizeModel = new SpinnerNumberModel();
            sizeModel.setMinimum(1);
            sizeModel.setValue(100);
            size.setModel(sizeModel);
            villagePanel.add(size);

            int response=JOptionPane.showOptionDialog(mapGUI, villagePanel, "Village creation"
                                          ,JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{"OK", "Cancel"}, JOptionPane.YES_OPTION);

            if(response==JOptionPane.YES_OPTION){
                int id = map.addVillage(name.getText(), (Integer)size.getValue());
                // don't know why coors have inconsistent offsets
                // so village stays centered(ish) on mouse
                map.getVillage(id).setX(e.getX() - Village.DIAMETER / 2);
                map.getVillage(id).setY(e.getY() - Village.DIAMETER);
                refresh();
            }
            state = NEUTRAL;
            refresh();
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
            refresh();
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


    //@todo: move to either LinkedList or MyList (probably LinkedList)
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