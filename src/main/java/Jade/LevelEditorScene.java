package Jade;


import org.lwjgl.BufferUtils;
import renderer.Shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene
{


    private int vertexID, fragmentID, shaderProgram;

    private float[] vertexArray = {
            //position,             colour
             0.5f, -0.5f,  0.0f,        1.0f, 0.0f, 0.0f, 1.0f,  //bottom right
            -0.5f,  0.5f,  0.0f,        0.0f, 1.0f, 0.0f, 1.0f,  //top left
             0.5f,  0.5f,  0.0f,        0.0f, 0.0f, 1.0f, 1.0f,  //top right
            -0.5f, -0.5f,  0.0f,        1.0f, 1.0f, 0.0f, 1.0f   //bottom left
    };

    //must be in counter clockwise order
    private int[] elementArray = {
            2, 1, 0,        // top right triangle
            0, 1, 3         // bottom left triangle
    };

    private int vaoID, vboID, eboID;

    private Shader defaultShader;

    public LevelEditorScene()
    {
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();
    }

    //compilation and linking for the shaders
    @Override
    public void init()
    {

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // create float buffer vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // create VBO and uplaod vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // creating the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // add the vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);

    }
    @Override
    public void update(float dt)
    {
        // binding the shader program
        defaultShader.use();

        //binding the VAO
        glBindVertexArray(vaoID);

        //enable vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        //unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);        //basically binding nothing
        defaultShader.detach();      //basically use program nothing
    }

}
