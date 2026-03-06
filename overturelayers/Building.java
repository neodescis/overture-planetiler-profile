package overturelayers;

import java.util.List;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.FeatureMerge;
import com.onthegomap.planetiler.ForwardingProfile.LayerPostProcessor;
import com.onthegomap.planetiler.VectorTile.Feature;
import com.onthegomap.planetiler.geo.GeometryException;
import com.onthegomap.planetiler.reader.SourceFeature;

/**
 * Overture data handler for the buildings
 * https://docs.overturemaps.org/schema/reference/buildings/building/ and
 * building parts
 * https://docs.overturemaps.org/schema/reference/buildings/building_part/
 * 
 * Run from Overture.java in the root
 */
public class Building extends BaseLayer implements LayerPostProcessor {
  public Building() {
    // output layer='buildings/building' and filter to input features where type in
    // ('building', 'building_part')
    super("buildings/building", List.of("building", "building_part"));
  }

  @Override
  public void processFeature(SourceFeature source, FeatureCollector features) {
    boolean isPart = source.hasTag("type", "building_part");
    features.polygon(LAYER)
      .setMinZoom(isPart ? 14 : 13)
      .setMinPixelSize(2)
      .inheritAttrsFromSource("id",
        "update_time",
        "subtype",
        "class",
        "level",
        "height",
        "num_floors",
        "min_height",
        "min_floor",
        "facade_color",
        "facade_material",
        "roof_color",
        "roof_material")
      .setSortKey((int) (source.getStruct("height").orElse(0).asDouble() * 10))
      .setAttr("parts", isPart ? "is" : source.getBoolean("has_parts") ? "has" : null)
      .putAttrs(names(source));
  }

  @Override
  public List<Feature> postProcess(int zoom, List<Feature> items) throws GeometryException {
    return FeatureMerge.mergeMultiPolygon(items);
  }
}
