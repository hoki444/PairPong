package com.algy.schedcore.frontend.idl.reflection.templates;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.SchedcoreRuntimeError;
import com.algy.schedcore.frontend.idl.IDLGameContext;
import com.algy.schedcore.frontend.idl.reflection.IDLCompTemplate;
import com.algy.schedcore.frontend.idl.reflection.NotModifiable;
import com.algy.schedcore.frontend.idl.reflection.SelectiveGroup;
import com.algy.schedcore.middleend.ModelFactoryComp;
import com.algy.schedcore.middleend.asset.AssetList;
import com.algy.schedcore.middleend.asset.AssetProvider;
import com.algy.schedcore.middleend.asset.ModelFactory;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class SimpleModelCompTemplate extends IDLCompTemplate {
    public static class Arrow {
        Vector3 from;
        Vector3 to;
    }

    public static class LineGrid {
        int xDivisions;
        int zDivisions;
        float xSize;
        float zSize;
    }

    public static class ModelShape {
        public float width;
        public float height;
        public float depth;
        public int divisions = 10;
    }

    public static class Capsule {
        public float radius;
        public float height;
        public int divisions = 10;
    }
    
    public static class Coord {
        float axisLength = 1;
    }
    public static class MaterialSig {
        public Color diffuse = new Color();
        public Color specular = new Color();
        public Color ambient = new Color();
        public float shiness = 0.3f;
        public String texture = null;
        public float textureU1 = 0;
        public float textureV1 = 0;
        public float textureU2 = 1;
        public float textureV2 = 1;
    }

    public static SelectiveGroup _group = 
            new SelectiveGroup("arrow", "box", 
                               "capsule", "cone", "cylinder", 
                               "lineGrid", "sphere", "XYZCoordinates");
    public static NotModifiable nm = 
            new NotModifiable("arrow", "box", 
                              "capsule", "cone", "cylinder", 
                              "lineGrid", "sphere", "XYZCoordinates", "material");
    public MaterialSig material = new MaterialSig();
    public Matrix4 localTransform = new Matrix4();
    public Arrow arrow;
    public ModelShape box;
    public Capsule capsule;
    public ModelShape cone;
    public ModelShape cylinder;
    public LineGrid lineGrid;
    public ModelShape sphere;
    public Coord XYZCoordinates;
    
    private Model makeModelWithMaterial (Material mat, long attributes) {
        ModelBuilder mb = new ModelBuilder();
        if (arrow != null) {
            return mb.createArrow(arrow.from, arrow.to, mat, attributes);
        } else if (box != null) {
            return mb.createBox(box.width, box.height, box.depth, mat, attributes);
        } else if (capsule != null) {
            return mb.createCapsule(capsule.radius, capsule.height, capsule.divisions, mat, attributes);
        } else if (cone != null) {
            return mb.createCone(cone.width, cone.height, cone.depth, cone.divisions, mat, attributes);
        } else if (cylinder != null) {
            return mb.createCylinder(cylinder.width, 
                                     cylinder.height, 
                                     cylinder.depth, 
                                     cylinder.divisions, mat, attributes);
        } else if (lineGrid != null) {
            return mb.createLineGrid(lineGrid.xDivisions, 
                                     lineGrid.zDivisions,
                                     lineGrid.xSize, 
                                     lineGrid.zSize, 
                                     mat, attributes);
        } else if (sphere != null) {
            return mb.createSphere(sphere.width, 
                                   sphere.height, 
                                   sphere.depth, 
                                   sphere.divisions, 
                                   sphere.divisions, 
                                   mat, attributes);
        } else if (XYZCoordinates != null) {
            return mb.createXYZCoordinates(XYZCoordinates.axisLength, mat, attributes);
        } else
            throw new SchedcoreRuntimeError("NON REACHABLE");
    }

    @Override
    protected BaseComp create(IDLGameContext context) {
        final long usageAttributes;
        final Array<Attribute> materialAttributes = new Array<Attribute>();
        materialAttributes.add(ColorAttribute.createDiffuse(material.diffuse));
        materialAttributes.add(ColorAttribute.createSpecular(material.specular));
        materialAttributes.add(ColorAttribute.createAmbient(material.ambient));
        materialAttributes.add(FloatAttribute.createShininess(material.shiness));

        final String texName;
        final float u1, v1, u2, v2;
        if (material.texture != null) {
            usageAttributes = Usage.Position | Usage.Normal | Usage.TextureCoordinates;
            texName = material.texture;
            u1 = material.textureU1;
            v1 = material.textureV1;
            u2 = material.textureU2;
            v2 = material.textureV2;
        } else {
            usageAttributes  = Usage.Position | Usage.Normal;
            texName = null;
            u1 = 0;
            v1 = 0;
            u2 = 0;
            v2 = 0;
        }

        return new ModelFactoryComp(new ModelFactory() {
            private Material makeMaterial (AssetProvider provider) {
                Array<Attribute> myMaterialAttr = new Array<Attribute>(materialAttributes);
                if (texName != null) {
                    myMaterialAttr.add(TextureAttribute
                                       .createDiffuse(new TextureRegion(provider.get(texName, Texture.class), u1, v1, u2, v2)));
                } 
                return new Material(myMaterialAttr);
            }
            @Override
            public Model make(AssetProvider provider) {
                return makeModelWithMaterial(makeMaterial(provider), usageAttributes);
            }
            @Override
            public void declareAsset(AssetList assetListOut) {
                if (texName != null) {
                    assetListOut.add(texName, Texture.class);
                }
            }
        }, localTransform);
    }

    @Override
    protected void modify(IDLGameContext context, BaseComp comp) {
    }
}