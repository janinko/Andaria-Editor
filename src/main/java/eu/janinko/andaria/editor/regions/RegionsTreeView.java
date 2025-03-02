package eu.janinko.andaria.editor.regions;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import eu.janinko.andaria.editor.regions.RegionsTreeView.RegionItem;
import eu.janinko.andaria.spherescript.sphere.objects.Areadef;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class RegionsTreeView extends TreeView<RegionItem>{

    public RegionsTreeView() {
    }

    public void init(Collection<Areadef> areas){
        fillData(areas);
    }

    private void fillData(Collection<Areadef> areas) {
        TreeItem<RegionItem> root = new TreeItem<>(new RegionItem("Regiony"));
        root.setExpanded(true);
        ObservableList<TreeItem<RegionItem>> rootChildren = root.getChildren();
        
        
        Map<String, Set<RegionItem>> items = new TreeMap<>();
        for (Areadef area : areas) {
            String group = area.getGroup();
            if(group == null) group = "<null>";
            Set<RegionItem> set = items.computeIfAbsent(group, k -> new TreeSet<>());
            RegionItem areadefItem = new RegionItem(area);
            
            set.add(areadefItem);
        }
        
        for (Map.Entry<String, Set<RegionItem>> e : items.entrySet()) {
            String groupName = e.getKey();
            Set<RegionItem> list = e.getValue();
            TreeItem<RegionItem> group = new TreeItem<>(new RegionItem(groupName));
            rootChildren.add(group);
            ObservableList<TreeItem<RegionItem>> children = group.getChildren();
            for (RegionItem item : list) {
                children.add(new TreeItem<>(item));
            }
        }
        this.setRoot(root);
        this.setShowRoot(false);
    }

    public static class RegionItem implements Comparable<RegionItem> {

        @Getter
        private String name;
        
        @Getter
        private final Areadef area;

        public RegionItem(String name) {
            setName(name);
            this.area = null;
        }
        
        public RegionItem(Areadef area) {
            setName(area.getName());
            this.area = area;
        }
        
        public final void setName(String name){
            if(name == null){
                this.name = "<null>";
            }else{
                this.name = name;
            }
        }
        
        public boolean isGroup(){
            return area == null;
        }

        @Override
        public int compareTo(RegionItem other) {
            int comp = this.name.compareTo(other.name);
            if(comp == 0 && area != null){
                comp = this.area.getDefname().compareTo(other.area.getDefname());
            }
            return comp;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 89 * hash + Objects.hashCode(this.name);
            hash = 89 * hash + Objects.hashCode(this.area);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            final RegionItem other = (RegionItem) obj;
            if (!Objects.equals(this.name, other.name)) return false;
            if (!Objects.equals(this.area, other.area)) return false;
            return true;
        }

    }
}
