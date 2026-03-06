package overturelayers;

import java.util.List;

import com.onthegomap.planetiler.FeatureCollector;
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
public class Place extends BaseLayer implements LayerPostProcessor {
  public Place() {
    // output layer='places/place' and filter to input features where type='place'
    super("places/place", List.of("place"));
  }

  @Override
  public void processFeature(SourceFeature source, FeatureCollector features) {
    var taxonomyHierarchy = source.getStruct("taxonomy").get("hierarchy");
    features.point(LAYER)
      .setMinZoom(14)
      .inheritAttrsFromSource("id",
        "update_time",
        "confidence",
        "operating_status")
      .setAttr("emails", source.hasTag("emails") ? source.getStruct("emails").asJson() : null)
      .setAttr("phones", source.hasTag("phones") ? source.getStruct("phones").asJson() : null)
      .setAttr("socials", source.hasTag("socials") ? source.getStruct("socials").asJson() : null)
      .setAttr("websites", source.hasTag("websites") ? source.getStruct("websites").asJson() : null)
      .setAttr("addresses", source.hasTag("addresses") ? source.getStruct("addresses").asJson() : null)
      .setAttr("sources", source.hasTag("sources") ? source.getStruct("sources").asJson() : null)
      .setAttr("categories.main", source.getStruct("taxonomy").get("primary").asString())
      .setAttr("categories.hierarchy", taxonomyHierarchy.rawValue() != null ? taxonomyHierarchy.asJson() : null)
      .putAttrs(names(source));
  }

  @Override
  public List<Feature> postProcess(int zoom, List<Feature> items) throws GeometryException {
    return items;
  }
}
