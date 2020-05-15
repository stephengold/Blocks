package com.rvandoosselaer.blocks;

import com.jme3.asset.DesktopAssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.simsilica.mathd.Vec3i;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * @author: rvandoosselaer
 */
public class WedgeTest {

    @BeforeAll
    public static void setUp() {
        BlocksConfig.initialize(new DesktopAssetManager(true));
    }

    @Test
    public void testDirection() {
        // when the wedge is rotated to the west, the north face is now facing east
        Block block = Block.builder()
                .name("my-block")
                .type("grass")
                .shape(ShapeIds.WEDGE_WEST)
                .usingMultipleImages(true)
                .build();

        Chunk chunk = Chunk.createAt(new Vec3i(0, 0, 0));
        chunk.addBlock(0, 0, 0, block);
        chunk.createNode(BlocksConfig.getInstance().getChunkMeshGenerator());

        Mesh mesh = ((Geometry) chunk.getNode().getChild("grass")).getMesh();

        // vertices of the east face
        List<Vector3f> positions = TestHelper.getPositions(mesh.getBuffer(VertexBuffer.Type.Position));
        int index1 = positions.indexOf(new Vector3f(0.5f, -0.5f, -0.5f));
        int index2 = positions.indexOf(new Vector3f(0.5f, 0.5f, -0.5f));
        int index3 = positions.indexOf(new Vector3f(0.5f, -0.5f, 0.5f));
        int index4 = positions.indexOf(new Vector3f(0.5f, 0.5f, 0.5f));

        // test that those vertices exist in the mesh
        assertNotEquals(index1, -1);
        assertNotEquals(index2, -1);
        assertNotEquals(index3, -1);
        assertNotEquals(index4, -1);
    }

    @Test
    public void testDoNotRenderSharedFace() {
        Block block = Block.builder()
                .name("my-block")
                .type(TypeIds.BIRCH_LOG)
                .shape(ShapeIds.WEDGE_INVERTED_EAST)
                .usingMultipleImages(true)
                .build();

        Chunk chunk = Chunk.createAt(new Vec3i(0, 0, 0));
        chunk.addBlock(0, 0, 0, BlocksConfig.getInstance().getBlockRegistry().get(BlockIds.GRASS));
        chunk.addBlock(1, 0, 0, block);
        chunk.createNode(BlocksConfig.getInstance().getChunkMeshGenerator());

        Mesh wedgeMesh = ((Geometry) chunk.getNode().getChild("birch_log")).getMesh();
        // the shared face between the cube and the wedge should not exist. The wedge shape should have 6 triangles.
        assertEquals(wedgeMesh.getTriangleCount(), 6);

        // when the cube is removed, the wedge should have all 8 triangles
        chunk.removeBlock(0, 0, 0);
        chunk.createNode(BlocksConfig.getInstance().getChunkMeshGenerator());
        wedgeMesh = ((Geometry) chunk.getNode().getChild("birch_log")).getMesh();
        assertEquals(wedgeMesh.getTriangleCount(), 8);
    }
}
