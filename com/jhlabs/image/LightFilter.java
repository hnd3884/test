package com.jhlabs.image;

import com.jhlabs.vecmath.Tuple4f;
import java.awt.image.Kernel;
import com.jhlabs.vecmath.Tuple3f;
import com.jhlabs.math.ImageFunction2D;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import com.jhlabs.vecmath.Color4f;
import com.jhlabs.vecmath.Vector3f;
import java.awt.Image;
import com.jhlabs.math.Function2D;
import java.util.Vector;

public class LightFilter extends WholeImageFilter
{
    public static final int COLORS_FROM_IMAGE = 0;
    public static final int COLORS_CONSTANT = 1;
    public static final int BUMPS_FROM_IMAGE = 0;
    public static final int BUMPS_FROM_IMAGE_ALPHA = 1;
    public static final int BUMPS_FROM_MAP = 2;
    public static final int BUMPS_FROM_BEVEL = 3;
    private float bumpHeight;
    private float bumpSoftness;
    private int bumpShape;
    private float viewDistance;
    Material material;
    private Vector lights;
    private int colorSource;
    private int bumpSource;
    private Function2D bumpFunction;
    private Image environmentMap;
    private int[] envPixels;
    private int envWidth;
    private int envHeight;
    private Vector3f l;
    private Vector3f v;
    private Vector3f n;
    private Color4f shadedColor;
    private Color4f diffuse_color;
    private Color4f specular_color;
    private Vector3f tmpv;
    private Vector3f tmpv2;
    protected static final float r255 = 0.003921569f;
    public static final int AMBIENT = 0;
    public static final int DISTANT = 1;
    public static final int POINT = 2;
    public static final int SPOT = 3;
    
    public LightFilter() {
        this.viewDistance = 10000.0f;
        this.colorSource = 0;
        this.bumpSource = 0;
        this.envWidth = 1;
        this.envHeight = 1;
        this.lights = new Vector();
        this.addLight(new DistantLight());
        this.bumpHeight = 1.0f;
        this.bumpSoftness = 5.0f;
        this.bumpShape = 0;
        this.material = new Material();
        this.l = new Vector3f();
        this.v = new Vector3f();
        this.n = new Vector3f();
        this.shadedColor = new Color4f();
        this.diffuse_color = new Color4f();
        this.specular_color = new Color4f();
        this.tmpv = new Vector3f();
        this.tmpv2 = new Vector3f();
    }
    
    public void setMaterial(final Material material) {
        this.material = material;
    }
    
    public Material getMaterial() {
        return this.material;
    }
    
    public void setBumpFunction(final Function2D bumpFunction) {
        this.bumpFunction = bumpFunction;
    }
    
    public Function2D getBumpFunction() {
        return this.bumpFunction;
    }
    
    public void setBumpHeight(final float bumpHeight) {
        this.bumpHeight = bumpHeight;
    }
    
    public float getBumpHeight() {
        return this.bumpHeight;
    }
    
    public void setBumpSoftness(final float bumpSoftness) {
        this.bumpSoftness = bumpSoftness;
    }
    
    public float getBumpSoftness() {
        return this.bumpSoftness;
    }
    
    public void setBumpShape(final int bumpShape) {
        this.bumpShape = bumpShape;
    }
    
    public int getBumpShape() {
        return this.bumpShape;
    }
    
    public void setViewDistance(final float viewDistance) {
        this.viewDistance = viewDistance;
    }
    
    public float getViewDistance() {
        return this.viewDistance;
    }
    
    public void setEnvironmentMap(final BufferedImage environmentMap) {
        this.environmentMap = environmentMap;
        if (environmentMap != null) {
            this.envWidth = environmentMap.getWidth();
            this.envHeight = environmentMap.getHeight();
            this.envPixels = this.getRGB(environmentMap, 0, 0, this.envWidth, this.envHeight, null);
        }
        else {
            final int n = 1;
            this.envHeight = n;
            this.envWidth = n;
            this.envPixels = null;
        }
    }
    
    public Image getEnvironmentMap() {
        return this.environmentMap;
    }
    
    public void setColorSource(final int colorSource) {
        this.colorSource = colorSource;
    }
    
    public int getColorSource() {
        return this.colorSource;
    }
    
    public void setBumpSource(final int bumpSource) {
        this.bumpSource = bumpSource;
    }
    
    public int getBumpSource() {
        return this.bumpSource;
    }
    
    public void setDiffuseColor(final int diffuseColor) {
        this.material.diffuseColor = diffuseColor;
    }
    
    public int getDiffuseColor() {
        return this.material.diffuseColor;
    }
    
    public void addLight(final Light light) {
        this.lights.addElement(light);
    }
    
    public void removeLight(final Light light) {
        this.lights.removeElement(light);
    }
    
    public Vector getLights() {
        return this.lights;
    }
    
    protected void setFromRGB(final Color4f c, final int argb) {
        c.set((argb >> 16 & 0xFF) * 0.003921569f, (argb >> 8 & 0xFF) * 0.003921569f, (argb & 0xFF) * 0.003921569f, (argb >> 24 & 0xFF) * 0.003921569f);
    }
    
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        int index = 0;
        final int[] outPixels = new int[width * height];
        final float width2 = Math.abs(6.0f * this.bumpHeight);
        final boolean invertBumps = this.bumpHeight < 0.0f;
        final Vector3f position = new Vector3f(0.0f, 0.0f, 0.0f);
        final Vector3f viewpoint = new Vector3f(width / 2.0f, height / 2.0f, this.viewDistance);
        final Vector3f normal = new Vector3f();
        final Color4f envColor = new Color4f();
        final Color4f diffuseColor = new Color4f(new Color(this.material.diffuseColor));
        final Color4f specularColor = new Color4f(new Color(this.material.specularColor));
        Function2D bump = this.bumpFunction;
        if (this.bumpSource == 0 || this.bumpSource == 1 || this.bumpSource == 2 || bump == null) {
            if (this.bumpSoftness != 0.0f) {
                int bumpWidth = width;
                int bumpHeight = height;
                int[] bumpPixels = inPixels;
                if (this.bumpSource == 2 && this.bumpFunction instanceof ImageFunction2D) {
                    final ImageFunction2D if2d = (ImageFunction2D)this.bumpFunction;
                    bumpWidth = if2d.getWidth();
                    bumpHeight = if2d.getHeight();
                    bumpPixels = if2d.getPixels();
                }
                final int[] tmpPixels = new int[bumpWidth * bumpHeight];
                final int[] softPixels = new int[bumpWidth * bumpHeight];
                final Kernel kernel = GaussianFilter.makeKernel(this.bumpSoftness);
                GaussianFilter.convolveAndTranspose(kernel, bumpPixels, tmpPixels, bumpWidth, bumpHeight, true, false, false, GaussianFilter.WRAP_EDGES);
                GaussianFilter.convolveAndTranspose(kernel, tmpPixels, softPixels, bumpHeight, bumpWidth, true, false, false, GaussianFilter.WRAP_EDGES);
                final Function2D bbump;
                bump = (bbump = new ImageFunction2D(softPixels, bumpWidth, bumpHeight, 1, this.bumpSource == 1));
                if (this.bumpShape != 0) {
                    bump = new Function2D() {
                        private Function2D original = bbump;
                        
                        public float evaluate(final float x, final float y) {
                            float v = this.original.evaluate(x, y);
                            switch (LightFilter.this.bumpShape) {
                                case 1: {
                                    v *= ImageMath.smoothStep(0.45f, 0.55f, v);
                                    break;
                                }
                                case 2: {
                                    v = ((v < 0.5f) ? 0.5f : v);
                                    break;
                                }
                                case 3: {
                                    v = ImageMath.triangle(v);
                                    break;
                                }
                                case 4: {
                                    v = ImageMath.circleDown(v);
                                    break;
                                }
                                case 5: {
                                    v = ImageMath.gain(v, 0.75f);
                                    break;
                                }
                            }
                            return v;
                        }
                    };
                }
            }
            else if (this.bumpSource != 2) {
                bump = new ImageFunction2D(inPixels, width, height, 1, this.bumpSource == 1);
            }
        }
        final float reflectivity = this.material.reflectivity;
        final float areflectivity = 1.0f - reflectivity;
        final Vector3f v1 = new Vector3f();
        final Vector3f v2 = new Vector3f();
        final Vector3f n = new Vector3f();
        final Light[] lightsArray = new Light[this.lights.size()];
        this.lights.copyInto(lightsArray);
        for (int i = 0; i < lightsArray.length; ++i) {
            lightsArray[i].prepare(width, height);
        }
        final float[][] heightWindow = new float[3][width];
        for (int x = 0; x < width; ++x) {
            heightWindow[1][x] = width2 * bump.evaluate((float)x, 0.0f);
        }
        for (int y = 0; y < height; ++y) {
            final boolean y2 = y > 0;
            final boolean y3 = y < height - 1;
            position.y = (float)y;
            for (int x2 = 0; x2 < width; ++x2) {
                heightWindow[2][x2] = width2 * bump.evaluate((float)x2, (float)(y + 1));
            }
            for (int x2 = 0; x2 < width; ++x2) {
                final boolean x3 = x2 > 0;
                final boolean x4 = x2 < width - 1;
                if (this.bumpSource != 3) {
                    int count = 0;
                    final Vector3f vector3f = normal;
                    final Vector3f vector3f2 = normal;
                    final Vector3f vector3f3 = normal;
                    final float x5 = 0.0f;
                    vector3f3.z = x5;
                    vector3f2.y = x5;
                    vector3f.x = x5;
                    final float m0 = heightWindow[1][x2];
                    final float m2 = x3 ? (heightWindow[1][x2 - 1] - m0) : 0.0f;
                    final float m3 = y2 ? (heightWindow[0][x2] - m0) : 0.0f;
                    final float m4 = x4 ? (heightWindow[1][x2 + 1] - m0) : 0.0f;
                    final float m5 = y3 ? (heightWindow[2][x2] - m0) : 0.0f;
                    if (x3 && y3) {
                        v1.x = -1.0f;
                        v1.y = 0.0f;
                        v1.z = m2;
                        v2.x = 0.0f;
                        v2.y = 1.0f;
                        v2.z = m5;
                        n.cross(v1, v2);
                        n.normalize();
                        if (n.z < 0.0) {
                            n.z = -n.z;
                        }
                        normal.add(n);
                        ++count;
                    }
                    if (x3 && y2) {
                        v1.x = -1.0f;
                        v1.y = 0.0f;
                        v1.z = m2;
                        v2.x = 0.0f;
                        v2.y = -1.0f;
                        v2.z = m3;
                        n.cross(v1, v2);
                        n.normalize();
                        if (n.z < 0.0) {
                            n.z = -n.z;
                        }
                        normal.add(n);
                        ++count;
                    }
                    if (y2 && x4) {
                        v1.x = 0.0f;
                        v1.y = -1.0f;
                        v1.z = m3;
                        v2.x = 1.0f;
                        v2.y = 0.0f;
                        v2.z = m4;
                        n.cross(v1, v2);
                        n.normalize();
                        if (n.z < 0.0) {
                            n.z = -n.z;
                        }
                        normal.add(n);
                        ++count;
                    }
                    if (x4 && y3) {
                        v1.x = 1.0f;
                        v1.y = 0.0f;
                        v1.z = m4;
                        v2.x = 0.0f;
                        v2.y = 1.0f;
                        v2.z = m5;
                        n.cross(v1, v2);
                        n.normalize();
                        if (n.z < 0.0) {
                            n.z = -n.z;
                        }
                        normal.add(n);
                        ++count;
                    }
                    final Vector3f vector3f4 = normal;
                    vector3f4.x /= count;
                    final Vector3f vector3f5 = normal;
                    vector3f5.y /= count;
                    final Vector3f vector3f6 = normal;
                    vector3f6.z /= count;
                }
                if (invertBumps) {
                    normal.x = -normal.x;
                    normal.y = -normal.y;
                }
                position.x = (float)x2;
                if (normal.z >= 0.0f) {
                    if (this.colorSource == 0) {
                        this.setFromRGB(diffuseColor, inPixels[index]);
                    }
                    else {
                        this.setFromRGB(diffuseColor, this.material.diffuseColor);
                    }
                    if (reflectivity != 0.0f && this.environmentMap != null) {
                        this.tmpv2.set(viewpoint);
                        this.tmpv2.sub(position);
                        this.tmpv2.normalize();
                        this.tmpv.set(normal);
                        this.tmpv.normalize();
                        this.tmpv.scale(2.0f * this.tmpv.dot(this.tmpv2));
                        this.tmpv.sub(this.v);
                        this.tmpv.normalize();
                        this.setFromRGB(envColor, this.getEnvironmentMap(this.tmpv, inPixels, width, height));
                        diffuseColor.x = reflectivity * envColor.x + areflectivity * diffuseColor.x;
                        diffuseColor.y = reflectivity * envColor.y + areflectivity * diffuseColor.y;
                        diffuseColor.z = reflectivity * envColor.z + areflectivity * diffuseColor.z;
                    }
                    final Color4f c = this.phongShade(position, viewpoint, normal, diffuseColor, specularColor, this.material, lightsArray);
                    final int alpha = inPixels[index] & 0xFF000000;
                    final int rgb = (int)(c.x * 255.0f) << 16 | (int)(c.y * 255.0f) << 8 | (int)(c.z * 255.0f);
                    outPixels[index++] = (alpha | rgb);
                }
                else {
                    outPixels[index++] = 0;
                }
            }
            final float[] t = heightWindow[0];
            heightWindow[0] = heightWindow[1];
            heightWindow[1] = heightWindow[2];
            heightWindow[2] = t;
        }
        return outPixels;
    }
    
    protected Color4f phongShade(final Vector3f position, final Vector3f viewpoint, final Vector3f normal, final Color4f diffuseColor, final Color4f specularColor, final Material material, final Light[] lightsArray) {
        this.shadedColor.set(diffuseColor);
        this.shadedColor.scale(material.ambientIntensity);
        for (int i = 0; i < lightsArray.length; ++i) {
            final Light light = lightsArray[i];
            this.n.set(normal);
            this.l.set(light.position);
            if (light.type != 1) {
                this.l.sub(position);
            }
            this.l.normalize();
            float nDotL = this.n.dot(this.l);
            if (nDotL >= 0.0) {
                float dDotL = 0.0f;
                this.v.set(viewpoint);
                this.v.sub(position);
                this.v.normalize();
                if (light.type == 3) {
                    dDotL = light.direction.dot(this.l);
                    if (dDotL < light.cosConeAngle) {
                        continue;
                    }
                }
                this.n.scale(2.0f * nDotL);
                this.n.sub(this.l);
                final float rDotV = this.n.dot(this.v);
                float rv;
                if (rDotV < 0.0) {
                    rv = 0.0f;
                }
                else {
                    rv = rDotV / (material.highlight - material.highlight * rDotV + rDotV);
                }
                if (light.type == 3) {
                    float e;
                    dDotL = (e = light.cosConeAngle / dDotL);
                    e *= e;
                    e *= e;
                    e *= e;
                    e = (float)Math.pow(dDotL, light.focus * 10.0f) * (1.0f - e);
                    rv *= e;
                    nDotL *= e;
                }
                this.diffuse_color.set(diffuseColor);
                this.diffuse_color.scale(material.diffuseReflectivity);
                final Color4f diffuse_color = this.diffuse_color;
                diffuse_color.x *= light.realColor.x * nDotL;
                final Color4f diffuse_color2 = this.diffuse_color;
                diffuse_color2.y *= light.realColor.y * nDotL;
                final Color4f diffuse_color3 = this.diffuse_color;
                diffuse_color3.z *= light.realColor.z * nDotL;
                this.specular_color.set(specularColor);
                this.specular_color.scale(material.specularReflectivity);
                final Color4f specular_color = this.specular_color;
                specular_color.x *= light.realColor.x * rv;
                final Color4f specular_color2 = this.specular_color;
                specular_color2.y *= light.realColor.y * rv;
                final Color4f specular_color3 = this.specular_color;
                specular_color3.z *= light.realColor.z * rv;
                this.diffuse_color.add(this.specular_color);
                this.diffuse_color.clamp(0.0f, 1.0f);
                this.shadedColor.add(this.diffuse_color);
            }
        }
        this.shadedColor.clamp(0.0f, 1.0f);
        return this.shadedColor;
    }
    
    private int getEnvironmentMap(final Vector3f normal, final int[] inPixels, final int width, final int height) {
        if (this.environmentMap != null) {
            final float angle = (float)Math.acos(-normal.y);
            float y = angle / 3.1415927f;
            float x;
            if (y == 0.0f || y == 1.0f) {
                x = 0.0f;
            }
            else {
                float f = normal.x / (float)Math.sin(angle);
                if (f > 1.0f) {
                    f = 1.0f;
                }
                else if (f < -1.0f) {
                    f = -1.0f;
                }
                x = (float)Math.acos(f) / 3.1415927f;
            }
            x = ImageMath.clamp(x * this.envWidth, 0.0f, (float)(this.envWidth - 1));
            y = ImageMath.clamp(y * this.envHeight, 0.0f, (float)(this.envHeight - 1));
            final int ix = (int)x;
            final int iy = (int)y;
            final float xWeight = x - ix;
            final float yWeight = y - iy;
            final int i = this.envWidth * iy + ix;
            final int dx = (ix != this.envWidth - 1) ? 1 : 0;
            final int dy = (iy == this.envHeight - 1) ? 0 : this.envWidth;
            return ImageMath.bilinearInterpolate(xWeight, yWeight, this.envPixels[i], this.envPixels[i + dx], this.envPixels[i + dy], this.envPixels[i + dx + dy]);
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return "Stylize/Light Effects...";
    }
    
    public static class Material
    {
        int diffuseColor;
        int specularColor;
        float ambientIntensity;
        float diffuseReflectivity;
        float specularReflectivity;
        float highlight;
        float reflectivity;
        float opacity;
        
        public Material() {
            this.opacity = 1.0f;
            this.ambientIntensity = 0.5f;
            this.diffuseReflectivity = 1.0f;
            this.specularReflectivity = 1.0f;
            this.highlight = 3.0f;
            this.reflectivity = 0.0f;
            this.diffuseColor = -7829368;
            this.specularColor = -1;
        }
        
        public void setDiffuseColor(final int diffuseColor) {
            this.diffuseColor = diffuseColor;
        }
        
        public int getDiffuseColor() {
            return this.diffuseColor;
        }
        
        public void setOpacity(final float opacity) {
            this.opacity = opacity;
        }
        
        public float getOpacity() {
            return this.opacity;
        }
    }
    
    public static class Light implements Cloneable
    {
        int type;
        Vector3f position;
        Vector3f direction;
        Color4f realColor;
        int color;
        float intensity;
        float azimuth;
        float elevation;
        float focus;
        float centreX;
        float centreY;
        float coneAngle;
        float cosConeAngle;
        float distance;
        
        public Light() {
            this(4.712389f, 0.5235988f, 1.0f);
        }
        
        public Light(final float azimuth, final float elevation, final float intensity) {
            this.type = 0;
            this.realColor = new Color4f();
            this.color = -1;
            this.focus = 0.5f;
            this.centreX = 0.5f;
            this.centreY = 0.5f;
            this.coneAngle = 0.5235988f;
            this.distance = 100.0f;
            this.azimuth = azimuth;
            this.elevation = elevation;
            this.intensity = intensity;
        }
        
        public void setAzimuth(final float azimuth) {
            this.azimuth = azimuth;
        }
        
        public float getAzimuth() {
            return this.azimuth;
        }
        
        public void setElevation(final float elevation) {
            this.elevation = elevation;
        }
        
        public float getElevation() {
            return this.elevation;
        }
        
        public void setDistance(final float distance) {
            this.distance = distance;
        }
        
        public float getDistance() {
            return this.distance;
        }
        
        public void setIntensity(final float intensity) {
            this.intensity = intensity;
        }
        
        public float getIntensity() {
            return this.intensity;
        }
        
        public void setConeAngle(final float coneAngle) {
            this.coneAngle = coneAngle;
        }
        
        public float getConeAngle() {
            return this.coneAngle;
        }
        
        public void setFocus(final float focus) {
            this.focus = focus;
        }
        
        public float getFocus() {
            return this.focus;
        }
        
        public void setColor(final int color) {
            this.color = color;
        }
        
        public int getColor() {
            return this.color;
        }
        
        public void setCentreX(final float x) {
            this.centreX = x;
        }
        
        public float getCentreX() {
            return this.centreX;
        }
        
        public void setCentreY(final float y) {
            this.centreY = y;
        }
        
        public float getCentreY() {
            return this.centreY;
        }
        
        public void prepare(final int width, final int height) {
            float lx = (float)(Math.cos(this.azimuth) * Math.cos(this.elevation));
            float ly = (float)(Math.sin(this.azimuth) * Math.cos(this.elevation));
            float lz = (float)Math.sin(this.elevation);
            (this.direction = new Vector3f(lx, ly, lz)).normalize();
            if (this.type != 1) {
                lx *= this.distance;
                ly *= this.distance;
                lz *= this.distance;
                lx += width * this.centreX;
                ly += height * this.centreY;
            }
            this.position = new Vector3f(lx, ly, lz);
            this.realColor.set(new Color(this.color));
            this.realColor.scale(this.intensity);
            this.cosConeAngle = (float)Math.cos(this.coneAngle);
        }
        
        public Object clone() {
            try {
                final Light copy = (Light)super.clone();
                return copy;
            }
            catch (final CloneNotSupportedException e) {
                return null;
            }
        }
        
        @Override
        public String toString() {
            return "Light";
        }
    }
    
    public class AmbientLight extends Light
    {
        @Override
        public String toString() {
            return "Ambient Light";
        }
    }
    
    public class PointLight extends Light
    {
        public PointLight() {
            this.type = 2;
        }
        
        @Override
        public String toString() {
            return "Point Light";
        }
    }
    
    public class DistantLight extends Light
    {
        public DistantLight() {
            this.type = 1;
        }
        
        @Override
        public String toString() {
            return "Distant Light";
        }
    }
    
    public class SpotLight extends Light
    {
        public SpotLight() {
            this.type = 3;
        }
        
        @Override
        public String toString() {
            return "Spotlight";
        }
    }
}
