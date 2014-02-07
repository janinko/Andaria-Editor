package eu.janinko.andaria.editor;

import eu.janinko.Andaria.ultimasdk.files.tiledata.ItemData;
import eu.janinko.Andaria.ultimasdk.files.tiledata.TileFlag;
import eu.janinko.Andaria.ultimasdk.files.tiledata.TileFlags;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 *
 * @author janinko
 */
public class TiledataEdit extends javax.swing.JFrame {
	private TiledataEditor te = new TiledataEditor();
	private int selected;

	/**
	 * Creates new form TiledataEdit
	 */
	public TiledataEdit() {
		initComponents();
	}

	private void load(){
		ItemData i = te.getTiledata(selected);

		System.out.println(i);

		animT.setText(Integer.toString(i.getAnimation()));
		heightT.setText(Integer.toString(i.getHeight()));
		hueT.setText(Integer.toString(i.getHue()));
		qualityT.setText(Integer.toString(i.getQuality()));
		quantityT.setText(Integer.toString(i.getQuantity()));
		unk1T.setText(Integer.toString(i.getUnknown1()));
		unk2T.setText(Integer.toString(i.getUnknown2()));
		unk3T.setText(Integer.toString(i.getUnknown3()));
		unk4T.setText(Integer.toString(i.getUnknown4()));
		valueT.setText(Integer.toString(i.getValue()));
		weightT.setText(Integer.toString(i.getWeight()));

		nameT.setText(i.getName());
		TileFlags f = i.getFlags();

		animFlag.setSelected(f.contains(TileFlag.Animation));
		armorFlag.setSelected(f.contains(TileFlag.Armor));
		artaFlag.setSelected(f.contains(TileFlag.ArticleA));
		artanFlag.setSelected(f.contains(TileFlag.ArticleAn));
		backgFlag.setSelected(f.contains(TileFlag.Background));
		bridgFlag.setSelected(f.contains(TileFlag.Bridge));
		contaFlag.setSelected(f.contains(TileFlag.Container));
		damagFlag.setSelected(f.contains(TileFlag.Damaing));
		doorFlag.setSelected(f.contains(TileFlag.Door));
		foliaFlag.setSelected(f.contains(TileFlag.Foliage));
		generiFlag.setSelected(f.contains(TileFlag.Generic));
		impasFlag.setSelected(f.contains(TileFlag.Impassable));
		internFlag.setSelected(f.contains(TileFlag.Internal));
		lightFlag.setSelected(f.contains(TileFlag.LightSource));
		liquiFlag.setSelected(f.contains(TileFlag.Liquid));
		mapFlag.setSelected(f.contains(TileFlag.Map));
		nodiagFlag.setSelected(f.contains(TileFlag.NoDiagonal));
		noshoFlag.setSelected(f.contains(TileFlag.NoShoot));
		partialFlag.setSelected(f.contains(TileFlag.PartialHue));
		roofFlag.setSelected(f.contains(TileFlag.Roof));
		stairbFlag.setSelected(f.contains(TileFlag.StairBack));
		stairrFlag.setSelected(f.contains(TileFlag.StairRight));
		surfaFlag.setSelected(f.contains(TileFlag.Surface));
		translFlag.setSelected(f.contains(TileFlag.Translucent));
		transpFlag.setSelected(f.contains(TileFlag.Transparent));
		unk1Flag.setSelected(f.contains(TileFlag.Unknown1));
		unk2Flag.setSelected(f.contains(TileFlag.Unknown2));
		unk3Flag.setSelected(f.contains(TileFlag.Unknown3));
		wallFlag.setSelected(f.contains(TileFlag.Wall));
		weapoFlag.setSelected(f.contains(TileFlag.Weapon));
		wearFlag.setSelected(f.contains(TileFlag.Wearable));
		windoFlag.setSelected(f.contains(TileFlag.Window));

		try {
			BufferedImage img = te.getImage(selected);
			if(img == null){
				image.setIcon(null);
			}else{
				ImageIcon icon = new ImageIcon(img);
				image.setIcon(icon);
			}
		} catch (IOException ex) {
			image.setIcon(null);
			Logger.getLogger(TiledataEdit.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	private void save(){
		ItemData i = te.getTiledata(selected);

		i.setAnimation((short) Integer.parseInt(animT.getText()));
		i.setHeight((byte) Integer.parseInt(heightT.getText()));
		i.setHue((byte) Integer.parseInt(hueT.getText()));
		i.setQuality((byte) Integer.parseInt(qualityT.getText()));
		i.setQuantity((byte) Integer.parseInt(quantityT.getText()));
		i.setUnknown1((short) Integer.parseInt(unk1T.getText()));
		i.setUnknown2((byte) Integer.parseInt(unk2T.getText()));
		i.setUnknown3((byte) Integer.parseInt(unk3T.getText()));
		i.setUnknown4((byte) Integer.parseInt(unk4T.getText()));
		i.setValue((byte) Integer.parseInt(valueT.getText()));
		i.setWeight((byte) Integer.parseInt(weightT.getText()));

		i.setName(nameT.getText());
		
		TileFlags f = new TileFlags();

		f.setFlag(TileFlag.Animation, animFlag.isSelected());
		f.setFlag(TileFlag.Armor, armorFlag.isSelected());
		f.setFlag(TileFlag.ArticleA, artaFlag.isSelected());
		f.setFlag(TileFlag.ArticleAn, artanFlag.isSelected());
		f.setFlag(TileFlag.Background, backgFlag.isSelected());
		f.setFlag(TileFlag.Bridge, bridgFlag.isSelected());
		f.setFlag(TileFlag.Container, contaFlag.isSelected());
		f.setFlag(TileFlag.Damaing, damagFlag.isSelected());
		f.setFlag(TileFlag.Door, doorFlag.isSelected());
		f.setFlag(TileFlag.Foliage, foliaFlag.isSelected());
		f.setFlag(TileFlag.Generic, generiFlag.isSelected());
		f.setFlag(TileFlag.Impassable, impasFlag.isSelected());
		f.setFlag(TileFlag.Internal, internFlag.isSelected());
		f.setFlag(TileFlag.LightSource, lightFlag.isSelected());
		f.setFlag(TileFlag.Liquid, liquiFlag.isSelected());
		f.setFlag(TileFlag.Map, mapFlag.isSelected());
		f.setFlag(TileFlag.NoDiagonal, nodiagFlag.isSelected());
		f.setFlag(TileFlag.NoShoot, noshoFlag.isSelected());
		f.setFlag(TileFlag.PartialHue, partialFlag.isSelected());
		f.setFlag(TileFlag.Roof, roofFlag.isSelected());
		f.setFlag(TileFlag.StairBack, stairbFlag.isSelected());
		f.setFlag(TileFlag.StairRight, stairrFlag.isSelected());
		f.setFlag(TileFlag.Surface, surfaFlag.isSelected());
		f.setFlag(TileFlag.Translucent, translFlag.isSelected());
		f.setFlag(TileFlag.Transparent, transpFlag.isSelected());
		f.setFlag(TileFlag.Unknown1, unk1Flag.isSelected());
		f.setFlag(TileFlag.Unknown2, unk2Flag.isSelected());
		f.setFlag(TileFlag.Unknown3, unk3Flag.isSelected());
		f.setFlag(TileFlag.Wall, wallFlag.isSelected());
		f.setFlag(TileFlag.Weapon, weapoFlag.isSelected());
		f.setFlag(TileFlag.Wearable, wearFlag.isSelected());
		f.setFlag(TileFlag.Window, windoFlag.isSelected());

		i.setFlags(f);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      selector = new javax.swing.JTextField();
      jButton1 = new javax.swing.JButton();
      image = new javax.swing.JLabel();
      jSeparator1 = new javax.swing.JSeparator();
      nameT = new javax.swing.JTextField();
      qualityT = new javax.swing.JTextField();
      weightT = new javax.swing.JTextField();
      quantityT = new javax.swing.JTextField();
      animT = new javax.swing.JTextField();
      hueT = new javax.swing.JTextField();
      valueT = new javax.swing.JTextField();
      jLabel2 = new javax.swing.JLabel();
      jLabel3 = new javax.swing.JLabel();
      jLabel4 = new javax.swing.JLabel();
      jLabel5 = new javax.swing.JLabel();
      jLabel6 = new javax.swing.JLabel();
      jLabel7 = new javax.swing.JLabel();
      heightT = new javax.swing.JTextField();
      unk1T = new javax.swing.JTextField();
      unk2T = new javax.swing.JTextField();
      unk4T = new javax.swing.JTextField();
      unk3T = new javax.swing.JTextField();
      jLabel8 = new javax.swing.JLabel();
      jLabel9 = new javax.swing.JLabel();
      jLabel10 = new javax.swing.JLabel();
      jLabel11 = new javax.swing.JLabel();
      jLabel12 = new javax.swing.JLabel();
      jSeparator2 = new javax.swing.JSeparator();
      backgFlag = new javax.swing.JCheckBox();
      weapoFlag = new javax.swing.JCheckBox();
      transpFlag = new javax.swing.JCheckBox();
      translFlag = new javax.swing.JCheckBox();
      wallFlag = new javax.swing.JCheckBox();
      damagFlag = new javax.swing.JCheckBox();
      impasFlag = new javax.swing.JCheckBox();
      liquiFlag = new javax.swing.JCheckBox();
      unk1Flag = new javax.swing.JCheckBox();
      surfaFlag = new javax.swing.JCheckBox();
      bridgFlag = new javax.swing.JCheckBox();
      jSeparator3 = new javax.swing.JSeparator();
      generiFlag = new javax.swing.JCheckBox();
      windoFlag = new javax.swing.JCheckBox();
      noshoFlag = new javax.swing.JCheckBox();
      artaFlag = new javax.swing.JCheckBox();
      artanFlag = new javax.swing.JCheckBox();
      internFlag = new javax.swing.JCheckBox();
      foliaFlag = new javax.swing.JCheckBox();
      partialFlag = new javax.swing.JCheckBox();
      unk2Flag = new javax.swing.JCheckBox();
      mapFlag = new javax.swing.JCheckBox();
      contaFlag = new javax.swing.JCheckBox();
      jSeparator4 = new javax.swing.JSeparator();
      wearFlag = new javax.swing.JCheckBox();
      lightFlag = new javax.swing.JCheckBox();
      animFlag = new javax.swing.JCheckBox();
      nodiagFlag = new javax.swing.JCheckBox();
      unk3Flag = new javax.swing.JCheckBox();
      armorFlag = new javax.swing.JCheckBox();
      roofFlag = new javax.swing.JCheckBox();
      doorFlag = new javax.swing.JCheckBox();
      stairbFlag = new javax.swing.JCheckBox();
      stairrFlag = new javax.swing.JCheckBox();
      jLabel13 = new javax.swing.JLabel();
      jButton2 = new javax.swing.JButton();
      jButton3 = new javax.swing.JButton();
      copyer = new javax.swing.JTextField();
      jButton4 = new javax.swing.JButton();
      jButton5 = new javax.swing.JButton();
      jButton6 = new javax.swing.JButton();

      setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
      setMinimumSize(new java.awt.Dimension(937, 522));

      jButton1.setText("Načti");
      jButton1.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton1ActionPerformed(evt);
         }
      });

      jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

      jLabel2.setText("Weight");

      jLabel3.setText("Quality");

      jLabel4.setText("Quantity");

      jLabel5.setText("Animation");

      jLabel6.setText("Hue");

      jLabel7.setText("Value");

      jLabel8.setText("Height");

      jLabel9.setText("Unknown1");

      jLabel10.setText("Unknown2");

      jLabel11.setText("Unknown3");

      jLabel12.setText("Unknown4");

      jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

      backgFlag.setText("Background");
      backgFlag.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            backgFlagActionPerformed(evt);
         }
      });

      weapoFlag.setText("Weapon");

      transpFlag.setText("Transparent");

      translFlag.setText("Translucent");

      wallFlag.setText("Wall");

      damagFlag.setText("Damaing");

      impasFlag.setText("Impassable");

      liquiFlag.setText("Liquid");

      unk1Flag.setText("Unknown1");

      surfaFlag.setText("Surface");

      bridgFlag.setText("Bridge");

      jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

      generiFlag.setText("Generic / Stackable");

      windoFlag.setText("Window");

      noshoFlag.setText("No shoot");

      artaFlag.setText("Article a");

      artanFlag.setText("Article an");

      internFlag.setText("Internal");

      foliaFlag.setText("Foliage");

      partialFlag.setText("Partial hue");

      unk2Flag.setText("Unknown2");

      mapFlag.setText("Map");

      contaFlag.setText("Container");

      jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);

      wearFlag.setText("Wearable");

      lightFlag.setText("Light source");

      animFlag.setText("Animation");

      nodiagFlag.setText("No diagonal");

      unk3Flag.setText("Unknown3");

      armorFlag.setText("Armor");

      roofFlag.setText("Roof");

      doorFlag.setText("Door");

      stairbFlag.setText("Stair back");

      stairrFlag.setText("Stair right");

      jLabel13.setText("080000000");

      jButton2.setText("Potvrď");
      jButton2.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton2ActionPerformed(evt);
         }
      });

      jButton3.setText("Ulož");
      jButton3.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton3ActionPerformed(evt);
         }
      });

      jButton4.setText("Zkopíruj");
      jButton4.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton4ActionPerformed(evt);
         }
      });

      jButton5.setText("->");
      jButton5.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton5ActionPerformed(evt);
         }
      });

      jButton6.setText("<-");
      jButton6.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton6ActionPerformed(evt);
         }
      });

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                  .addComponent(image, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addGroup(layout.createSequentialGroup()
                     .addComponent(selector, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                     .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                     .addComponent(jButton1)
                     .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                     .addComponent(jButton2)
                     .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                     .addComponent(jButton3)))
               .addGroup(layout.createSequentialGroup()
                  .addComponent(copyer, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jButton4)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 83, Short.MAX_VALUE)
                  .addComponent(jButton6)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jButton5)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addComponent(nameT)
               .addGroup(layout.createSequentialGroup()
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addGroup(layout.createSequentialGroup()
                        .addComponent(quantityT, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4))
                     .addGroup(layout.createSequentialGroup()
                        .addComponent(animT, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5))
                     .addGroup(layout.createSequentialGroup()
                        .addComponent(hueT, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6))
                     .addGroup(layout.createSequentialGroup()
                        .addComponent(valueT, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7))
                     .addGroup(layout.createSequentialGroup()
                        .addComponent(weightT, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2))
                     .addGroup(layout.createSequentialGroup()
                        .addComponent(qualityT, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3))
                     .addGroup(layout.createSequentialGroup()
                        .addComponent(heightT, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8))
                     .addGroup(layout.createSequentialGroup()
                        .addComponent(unk1T, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9))
                     .addGroup(layout.createSequentialGroup()
                        .addComponent(unk2T, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10))
                     .addGroup(layout.createSequentialGroup()
                        .addComponent(unk4T, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12))
                     .addGroup(layout.createSequentialGroup()
                        .addComponent(unk3T, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)))
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                  .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addComponent(backgFlag)
                     .addComponent(weapoFlag)
                     .addComponent(transpFlag)
                     .addComponent(translFlag)
                     .addComponent(wallFlag)
                     .addComponent(damagFlag)
                     .addComponent(impasFlag)
                     .addComponent(liquiFlag)
                     .addComponent(unk1Flag)
                     .addComponent(surfaFlag)
                     .addComponent(bridgFlag))
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                  .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addComponent(generiFlag)
                     .addComponent(windoFlag)
                     .addComponent(noshoFlag)
                     .addComponent(artaFlag)
                     .addComponent(artanFlag)
                     .addComponent(internFlag)
                     .addComponent(foliaFlag)
                     .addComponent(partialFlag)
                     .addComponent(unk2Flag)
                     .addComponent(mapFlag)
                     .addComponent(contaFlag))
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                  .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addComponent(wearFlag)
                     .addComponent(lightFlag)
                     .addComponent(animFlag)
                     .addComponent(nodiagFlag)
                     .addComponent(unk3Flag)
                     .addComponent(armorFlag)
                     .addComponent(roofFlag)
                     .addComponent(doorFlag)
                     .addComponent(stairbFlag)
                     .addComponent(stairrFlag)
                     .addComponent(jLabel13))
                  .addGap(0, 4, Short.MAX_VALUE)))
            .addContainerGap())
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addGroup(layout.createSequentialGroup()
                  .addComponent(nameT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addComponent(jSeparator2)
                     .addComponent(jSeparator3)
                     .addComponent(jSeparator4)
                     .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                           .addGroup(layout.createSequentialGroup()
                              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                 .addComponent(weightT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                 .addComponent(jLabel2))
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                 .addComponent(qualityT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                 .addComponent(jLabel3))
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                 .addComponent(quantityT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                 .addComponent(jLabel4))
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                 .addComponent(animT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                 .addComponent(jLabel5))
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                 .addComponent(hueT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                 .addComponent(jLabel6))
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                 .addComponent(valueT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                 .addComponent(jLabel7))
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                 .addComponent(heightT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                 .addComponent(jLabel8))
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                 .addComponent(unk1T, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                 .addComponent(jLabel9))
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                 .addComponent(unk2T, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                 .addComponent(jLabel10))
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                 .addComponent(unk3T, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                 .addComponent(jLabel11))
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                 .addComponent(unk4T, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                 .addComponent(jLabel12)))
                           .addGroup(layout.createSequentialGroup()
                              .addComponent(backgFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(weapoFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(transpFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(translFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(wallFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(damagFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(impasFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(liquiFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(unk1Flag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(surfaFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(bridgFlag))
                           .addGroup(layout.createSequentialGroup()
                              .addComponent(generiFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(windoFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(noshoFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(artaFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(artanFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(internFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(foliaFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(partialFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(unk2Flag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(mapFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(contaFlag))
                           .addGroup(layout.createSequentialGroup()
                              .addComponent(wearFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(lightFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(animFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(nodiagFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(unk3Flag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(armorFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(roofFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(doorFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(stairbFlag)
                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                              .addComponent(stairrFlag)
                              .addGap(18, 18, 18)
                              .addComponent(jLabel13)))
                        .addGap(0, 0, Short.MAX_VALUE))))
               .addGroup(layout.createSequentialGroup()
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                     .addComponent(selector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                     .addComponent(jButton1)
                     .addComponent(jButton2)
                     .addComponent(jButton3))
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                     .addComponent(copyer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                     .addComponent(jButton4)
                     .addComponent(jButton5)
                     .addComponent(jButton6))
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(image, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
               .addComponent(jSeparator1))
            .addContainerGap())
      );

      pack();
   }// </editor-fold>//GEN-END:initComponents

   private void backgFlagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backgFlagActionPerformed
      // TODO add your handling code here:
   }//GEN-LAST:event_backgFlagActionPerformed

   private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
      String text = selector.getText();
		if(text.startsWith("0")){
			selected = Integer.parseInt(text,16);
		}else{
			selected = Integer.parseInt(text);
		}
		load();
   }//GEN-LAST:event_jButton1ActionPerformed

   private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
      save();
   }//GEN-LAST:event_jButton2ActionPerformed

   private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
		try {
			te.save();
		} catch (IOException ex) {
			Logger.getLogger(TiledataEdit.class.getName()).log(Level.SEVERE, null, ex);
		}
   }//GEN-LAST:event_jButton3ActionPerformed

   private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
      String text = copyer.getText();
		int copy;
		if(text.startsWith("0")){
			copy = Integer.parseInt(text,16);
		}else{
			copy = Integer.parseInt(text);
		}
		te.copy(copy, selected);
		load();
   }//GEN-LAST:event_jButton4ActionPerformed

   private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
      selected--;
		selector.setText(Integer.toString(selected));
		load();
   }//GEN-LAST:event_jButton6ActionPerformed

   private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
      selected++;
		selector.setText(Integer.toString(selected));
		load();
   }//GEN-LAST:event_jButton5ActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(TiledataEdit.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(TiledataEdit.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(TiledataEdit.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(TiledataEdit.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new TiledataEdit().setVisible(true);
			}
		});
	}
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JCheckBox animFlag;
   private javax.swing.JTextField animT;
   private javax.swing.JCheckBox armorFlag;
   private javax.swing.JCheckBox artaFlag;
   private javax.swing.JCheckBox artanFlag;
   private javax.swing.JCheckBox backgFlag;
   private javax.swing.JCheckBox bridgFlag;
   private javax.swing.JCheckBox contaFlag;
   private javax.swing.JTextField copyer;
   private javax.swing.JCheckBox damagFlag;
   private javax.swing.JCheckBox doorFlag;
   private javax.swing.JCheckBox foliaFlag;
   private javax.swing.JCheckBox generiFlag;
   private javax.swing.JTextField heightT;
   private javax.swing.JTextField hueT;
   private javax.swing.JLabel image;
   private javax.swing.JCheckBox impasFlag;
   private javax.swing.JCheckBox internFlag;
   private javax.swing.JButton jButton1;
   private javax.swing.JButton jButton2;
   private javax.swing.JButton jButton3;
   private javax.swing.JButton jButton4;
   private javax.swing.JButton jButton5;
   private javax.swing.JButton jButton6;
   private javax.swing.JLabel jLabel10;
   private javax.swing.JLabel jLabel11;
   private javax.swing.JLabel jLabel12;
   private javax.swing.JLabel jLabel13;
   private javax.swing.JLabel jLabel2;
   private javax.swing.JLabel jLabel3;
   private javax.swing.JLabel jLabel4;
   private javax.swing.JLabel jLabel5;
   private javax.swing.JLabel jLabel6;
   private javax.swing.JLabel jLabel7;
   private javax.swing.JLabel jLabel8;
   private javax.swing.JLabel jLabel9;
   private javax.swing.JSeparator jSeparator1;
   private javax.swing.JSeparator jSeparator2;
   private javax.swing.JSeparator jSeparator3;
   private javax.swing.JSeparator jSeparator4;
   private javax.swing.JCheckBox lightFlag;
   private javax.swing.JCheckBox liquiFlag;
   private javax.swing.JCheckBox mapFlag;
   private javax.swing.JTextField nameT;
   private javax.swing.JCheckBox nodiagFlag;
   private javax.swing.JCheckBox noshoFlag;
   private javax.swing.JCheckBox partialFlag;
   private javax.swing.JTextField qualityT;
   private javax.swing.JTextField quantityT;
   private javax.swing.JCheckBox roofFlag;
   private javax.swing.JTextField selector;
   private javax.swing.JCheckBox stairbFlag;
   private javax.swing.JCheckBox stairrFlag;
   private javax.swing.JCheckBox surfaFlag;
   private javax.swing.JCheckBox translFlag;
   private javax.swing.JCheckBox transpFlag;
   private javax.swing.JCheckBox unk1Flag;
   private javax.swing.JTextField unk1T;
   private javax.swing.JCheckBox unk2Flag;
   private javax.swing.JTextField unk2T;
   private javax.swing.JCheckBox unk3Flag;
   private javax.swing.JTextField unk3T;
   private javax.swing.JTextField unk4T;
   private javax.swing.JTextField valueT;
   private javax.swing.JCheckBox wallFlag;
   private javax.swing.JCheckBox weapoFlag;
   private javax.swing.JCheckBox wearFlag;
   private javax.swing.JTextField weightT;
   private javax.swing.JCheckBox windoFlag;
   // End of variables declaration//GEN-END:variables
}
