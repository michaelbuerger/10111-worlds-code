package org.firstinspires.ftc.teamcode;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.DogeCV;
import com.disnodeteam.dogecv.detectors.roverrukus.GoldDetector;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.opencv.core.Point;

@Autonomous(name="TotemServoTester", group="Autonomous")
public class TotemServoTester extends LinearOpMode {

    NewHardware hw = new NewHardware();

    ElapsedTime runtime = null;

    @Override
    public void runOpMode() /* Run auto code */
    {
        hw.Initialize(hardwareMap, telemetry);

        if(isStopRequested())
        {
        }

        hw.totemServo.setPosition(hw.UP_TOTEM_POSITION);

        while(!opModeIsActive() && !isStopRequested())
        {
            telemetry.addLine("Waiting for start command...");
            telemetry.update();
        }

        if(opModeIsActive()) {
            runtime.reset();

            hw.totemServo.setPosition(hw.DOWN_TOTEM_POSITION);
        }

        while(opModeIsActive()) {
            telemetry.addLine("Maintaining WiFi Direct Connection");
            telemetry.update();
        }

        MotorEncoderRunner.StopAllRunners();
    }
}
