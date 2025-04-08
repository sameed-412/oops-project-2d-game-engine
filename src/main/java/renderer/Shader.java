package renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader
{
    private int shaderProgramID;

    private String vertexSource, fragmentSource, filepath;

    public Shader(String filepath)
    {
        this.filepath = filepath;
        try{
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // first pattern after #type <pattern>
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n" , index);
            String firstPattern = source.substring(index, eol).trim();

            // second pattern after #type <pattern>
            index = source.indexOf("#type" , eol) + 6;
            eol = source.indexOf("\r\n",index);
            String secondPattern = source.substring(index, eol).trim();

            if(firstPattern.equals("vertex"))
            {
                vertexSource = splitString[1];
            }
            else if(firstPattern.equals("fragment"))
            {
                fragmentSource = splitString[1];
            }
            else {
                throw new IOException("Unexpected token '" + firstPattern + "'");
            }

            if(secondPattern.equals("vertex"))
            {
                vertexSource = splitString[2];
            }
            else if(secondPattern.equals("fragment"))
            {
                fragmentSource = splitString[2];
            }
            else {
                throw new IOException("Unexpected token '" + secondPattern + "'");
            }
        }catch (IOException e){
            e.printStackTrace();
            assert false: "Error, could not open file for shader '" + filepath + "'";
        }

    }
    public void compile()
    {
        int vertexID, fragmentID;
        // loading and compiling of vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // passing the shader source to the gpu
        glShaderSource(vertexID , vertexSource);
        glCompileShader(vertexID);

        //checking for errors in the compilation process of the shader
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if(success == GL_FALSE)
        {
            int length = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + filepath + "'\n\tVertex shader compilation failed");
            System.out.println(glGetShaderInfoLog(vertexID , length));
            assert false: "";
        }

        // loading and compiling of fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // passing the shader source to the gpu
        glShaderSource(fragmentID , fragmentSource);
        glCompileShader(fragmentID);

        //checking for errors in the compilation process of the shader
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if(success == GL_FALSE)
        {
            int length = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + filepath + "'\n\tFragment shader compilation failed");
            System.out.println(glGetShaderInfoLog(fragmentID , length));
            assert false: "";
        }
        //link shaders and check for any errors
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        // checking for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if(success == GL_FALSE)
        {
            int length = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + filepath + "'\n\tLinking of shaders failed");
            System.out.println(glGetProgramInfoLog(shaderProgramID , length));
            assert false: "";
        }
    }
    public void use()
    {
        glUseProgram(shaderProgramID);
    }
    public void detach()
    {
        glUseProgram(0);
    }
}
