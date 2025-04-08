package Jade;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import util.Time;

import java.lang.module.ModuleDescriptor;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window
{
    private int width, height;
    private String title;
    private long glfwWindow;
    private boolean fadeToBlack = false;
    public float r,g,b,a;

    private static Window window = null;

    private static Scene currentScene;

    private Window()// private because only one window class so no other class can create a window
    {
        this.height = 1080;
        this.width = 1920;
        this.title = "C049";
        r = 1;
        g = 1;
        b = 1;
        a = 1;
    }
    public static void changeScene(int newScene)
    {
        switch (newScene)
        {
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                break;
            default:
                assert false: "Unknown Scene '" + newScene + "'";
        }
    }

    public static Window get()
    {
        if(Window.window == null)
            Window.window = new Window();
        return window;
    }
    public void run()
    {
        System.out.println("Hello LWJGL" + Version.getVersion() + "!");
        init();
        loop();

        //free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        //terminate glfw and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
    public void init()
    {
        GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit())
            throw new IllegalStateException("Unable to initialise GLFW");

        //configuring GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE , GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        //creating the window
        glfwWindow = glfwCreateWindow(this.width , this.height, this.title , NULL, NULL);
        if(glfwWindow == NULL)
        {
            throw new IllegalStateException("Failed to create a GLFW Window");
        }

        glfwSetCursorPosCallback(glfwWindow , MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow , MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow , MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        //making the opengl context current
        glfwMakeContextCurrent(glfwWindow);
        //enabling v-sync
        glfwSwapInterval(1);//swap it every single frame

        //making the window visible
        glfwShowWindow(glfwWindow);

        //this is required for the working of LWJGL with GLFW's OpenGL context
        //LWJGL detects the current context and makes the openGL bindings available to use
        // very important it will break without it
        GL.createCapabilities();

        Window.changeScene(0);
    }
    public void loop()
    {
        float beginTime = Time.getTime();
        float endTime;
        float dt = -1.0f;

        while(!glfwWindowShouldClose(glfwWindow))
        {
            //poll events
            glfwPollEvents();

            glClearColor(r , g , b , a);
            glClear(GL_COLOR_BUFFER_BIT);

            if(dt>=0)
            {
                currentScene.update(dt);
            }

            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }
}
