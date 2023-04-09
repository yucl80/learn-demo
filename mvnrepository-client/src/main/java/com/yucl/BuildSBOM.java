package com.yucl;

import org.cyclonedx.BomGeneratorFactory;
import org.cyclonedx.CycloneDxSchema;
import org.cyclonedx.generators.json.BomJsonGenerator;
import org.cyclonedx.model.Bom;

public class BuildSBOM {
    public static void main(String[] args) {
        Bom bom = new Bom();

        BomJsonGenerator data = BomGeneratorFactory.createJson(CycloneDxSchema.Version.VERSION_14, bom);
        data.toJsonString();
    }
}
