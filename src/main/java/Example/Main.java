package Example;


import com.zed.VoiceAssistantLibrary.Assistant;
import com.zed.VoiceAssistantLibrary.AssistantVoice;
import com.zed.VoiceAssistantLibrary.CommandInterface;
import com.zed.VoiceAssistantLibrary.SpeechListener;
import com.zed.VoiceAssistantLibrary.TextToIntent.Intent;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Main extends Assistant {

    @Override
    public void Initialize(Properties prop) {
        super.tti = super.InitializeTTI(prop.getProperty("commandJson"));
        super.tts = super.InitializeTTS(AssistantVoice.poppy);
        super.intentMap = this.MapCommands(prop);

        // initialize anything else after
        // ....
    }

    public void HandleIntent(Intent intent) {
        // if the command couldn't be parsed then intent is null
        if(intent == null) {
            this.tts.Speak("Sorry i didn't quite understand");
            return;
        }

        // check for mapping
        var command = this.intentMap.getOrDefault(intent.intent, null);
        if(command == null) {
            this.tts.Speak("Sorry no mapping for that command");
            return;
        }

        // execute command and speak the returned string
        this.tts.Speak(command.ExecuteCommand(intent));
    }

    @Override
    public void Run(Properties prop) {
        var wakeWord = prop.getProperty("wakeWord");
        var listener = new SpeechListener(wakeWord, this.tti)
                .setWakeWordCallback(() -> {
                    System.out.println("[INFO] :: Wake word Detected!");
                })
                .setIntentCallBack(this::HandleIntent);
//              Or you can use the intent callback like this
//                .setIntentCallBack(intent -> {
//                    HandleIntent(intent);
//                });
        try {
            listener.Start("model");
        } catch(IOException e) {
            // handle exception
            e.printStackTrace();
        }

    }

    @Override
    public Map<String, CommandInterface> MapCommands(Properties prop) {
        HashMap<String, CommandInterface> map = new HashMap<>();
        // map the 'getWeather' intent to the weather command
        map.put("getWeather", new WeatherCommand(prop));
        return map;
    }

    public static void main(String[] args) {

        if(args.length != 1) {
            System.out.println("Path to properties file is required");
            return;
        }


        try(InputStream stream = new FileInputStream(args[0])) {

            Properties prop = new Properties();
            prop.load(stream);

            var assistant = new Main();
            assistant.Initialize(prop);
            assistant.Run(prop);

        } catch(IOException e) {
            System.err.printf("Failed to get the properties file!: %s\n", e);
        }
    }
}