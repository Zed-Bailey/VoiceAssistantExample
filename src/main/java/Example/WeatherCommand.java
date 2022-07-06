package Example;


import com.zed.VoiceAssistantLibrary.CommandInterface;
import com.zed.VoiceAssistantLibrary.TextToIntent.Intent;

import java.util.Properties;
import java.util.Random;

public class WeatherCommand implements CommandInterface {

    public WeatherCommand(Properties prop) {

    }
    @Override
    public String ExecuteCommand(Intent intent) {
        var rand = new Random();
        var conditions = new String[] {"stormy", "windy", "raining", "sunny"};
        var temp = rand.nextInt(25);
        return String.format("The weather in %s is %s, current temperature is %d", intent.slots.get("location"), conditions[rand.nextInt(conditions.length)], temp);
    }
}
