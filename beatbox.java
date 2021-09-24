import java.util.ArrayList;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.swing.*;

import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.awt.*;

public class beatbox {
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;

    private ArrayList<ArrayList<checkbox>> head = new ArrayList<ArrayList<checkbox>>();
    private ArrayList<JLabel> l = new ArrayList<JLabel>();

    static private int beat_length = 16;
    static private int instrumentNum = 15;

    private JFrame frame = new JFrame("Cyber Beatbox");
    private Box left = new Box(BoxLayout.Y_AXIS);
    private JPanel center;
    private Box right = new Box(BoxLayout.Y_AXIS);

    private String[] instrumentName = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat","Acoustic Snare", "Crash Cymbal", "Hand Clap", "High Tom","Hi Bongo", "Maracas", "Whistle", "Low Conga", 
    "Cowbell", "Vibraslap", "Low-mid Tom", "High Agogo" };
    private int[] instruments = {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67};

    private JButton start = new JButton("Start");
    private JButton stop = new JButton("Stop");
    private JButton up = new JButton("Tempo Up");
    private JButton down = new JButton("Tempo Down");
    private JButton store = new JButton("serializelt");
    private JButton restore = new JButton("restore");
    private start_listener startl = new start_listener();
    private stop_listener stopl = new stop_listener();
    private up_listener upl = new up_listener();
    private down_listener downl = new down_listener();
    private store_listener storel = new store_listener();
    private restore_listener restorel = new restore_listener();

    beatbox(){
        //add listener to buttons
        start.addActionListener(startl);
        stop.addActionListener(stopl);
        up.addActionListener(upl);
        down.addActionListener(downl);
        store.addActionListener(storel);
        restore.addActionListener(restorel);

        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //add 3 panels to the frame
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        frame.setContentPane(p);
        GridLayout grid = new GridLayout(instrumentNum,beat_length);
        grid.setVgap(1);
        grid.setHgap(2);
        center = new JPanel(grid);
        frame.getContentPane().add(BorderLayout.WEST, left);
        frame.getContentPane().add(BorderLayout.CENTER, center);
        frame.getContentPane().add(BorderLayout.EAST, right);
        //add buttons to the right panel
        right.add(start);
        right.add(stop);
        right.add(up);
        right.add(down);
        right.add(store);
        right.add(restore);
        //add labels and checkboxes
        for(String name:instrumentName){
            //deal with label
            JLabel temp_label = new JLabel(name);
            left.add(temp_label);
            l.add(temp_label);
            //deal with checkbox

            ArrayList<checkbox> temp = new ArrayList<checkbox>();
            for(int i=0;i<beat_length;i++){
                checkbox temp_box = new checkbox();
                center.add(temp_box.checkBox);
                temp.add(temp_box);
            }
            head.add(temp);
        }
        frame.setBounds(50, 50, 300, 300);
        frame.pack();
        frame.setVisible(true);
        initMidi();
        }

    void initMidi(){
        try {
            sequencer = MidiSystem.getSequencer();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
            sequencer.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    MidiEvent makeEvent(int comm,int chan, int one, int two, int tick){
        try {
            ShortMessage shortMessage = new ShortMessage();
            shortMessage.setMessage(comm, chan, one, two);
            MidiEvent midiEvent = new MidiEvent(shortMessage, tick);
            return midiEvent;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    void buildandstart(){
        sequence.deleteTrack(track);
        track = sequence.createTrack();
        for(int i=0;i<instrumentNum;i++){
            track.add(makeEvent(192, i+1, instruments[i], 0, 1));}
        for(int i=0;i<beat_length;i++){
            for(int j=0;j<instrumentNum;j++){
                if(head.get(j).get(i).getstate()){
                    track.add(makeEvent(144, j, 44, 100, i+1));
                    track.add(makeEvent(128, j, 44, 100, i+2));
                }
            }
        }
        track.add(makeEvent(176, 1, 127, 0, 16));
        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    

    class start_listener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            buildandstart();
        }
    }

    class stop_listener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            sequencer.stop();
        }
    }

    class up_listener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            float tempofactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempofactor*1.03));
        }
    }

    class down_listener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            float tempofactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempofactor*0.97));
        }
    }

    class store_listener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                File file = new File("E:/song.ser");
                FileOutputStream writer = new FileOutputStream(file,true);
                ObjectOutputStream os = new ObjectOutputStream(writer);
                os.writeObject(head);
                os.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    class restore_listener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            sequencer.stop();
            ArrayList<ArrayList<checkbox>> temp = null;
            try {
                File file = new File("E:/song.ser");
                FileInputStream reader = new FileInputStream(file);
                ObjectInputStream os = new ObjectInputStream(reader);
                temp = (ArrayList<ArrayList<checkbox>>) os.readObject();
                for(int i=0;i<beat_length;i++){
                    for(int j=0;j<instrumentNum;j++){
                        if(temp.get(j).get(i).getstate()){
                            head.get(j).get(i).setstate(true);
                            head.get(j).get(i).checkBox.setSelected(true);
                        }else{
                            head.get(j).get(i).setstate(false);
                            head.get(j).get(i).checkBox.setSelected(false);
                        }
                    }
                }os.close();


            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        beatbox b = new beatbox();
}

}
