package org.firstinspires.ftc.teamcode;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.DogeCV;
import com.disnodeteam.dogecv.detectors.roverrukus.GoldDetector;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.opencv.core.Point;

@Autonomous(name="Crater : Alpha", group="Autonomous")
public class AutoCraterAlpha extends LinearOpMode {

    NewHardware hw = new NewHardware();
    MotorEncoderRunner motorEncoderRunner = null;

    ElapsedTime runtime = null;

    GoldDetector detector;

    @Override
    public void runOpMode() /* Run auto code */
    {
        MotorEncoderMovement.telemetry = telemetry;
        MotorEncoderRunner.telemetry = telemetry;

        hw.Initialize(hardwareMap);
        motorEncoderRunner = new MotorEncoderRunner(this);

        hw.totemServo.setPosition(hw.UP_TOTEM_POSITION);

        runtime = new ElapsedTime();

        /* DogeCV Stuff */
        detector = new GoldDetector(); // Create detector

        //detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance(), DogeCV.CameraMode.BACK, false, hw.sampleCamera); // Initialize it with the app context and camera
        detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance(), DogeCV.CameraMode.BACK, false);
        detector.useDefaults();

        // Optional tuning
        detector.downscale = 0.4; // How much to downscale the input frames

        detector.areaScoringMethod = DogeCV.AreaScoringMethod.MAX_AREA; // Can also be PERFECT_AREA
        //detector.perfectAreaScorer.perfectArea = 10000; // if using PERFECT_AREA scoring
        detector.maxAreaScorer.weight = 0.005; //

        detector.ratioScorer.weight = 5; //
        detector.ratioScorer.perfectRatio = 1.0; // Ratio adjustment

        /* DogeCV Stuff */

        if(isStopRequested())
        {
            detector.disable();
            MotorEncoderRunner.StopAllRunners();
        }

        waitForStart();
        runtime.reset();

        detector.enable();

        sleep(1000); // TODO: Test out different times, find minimum possible for sampling to work properly

        Point goldPosPixels = detector.getScreenPosition();
        telemetry.addData("Position", goldPosPixels.x);

        String goldPos = "LEFT";

        if(goldPosPixels.x < detector.downscaleResolution.width/3) // If mineral LEFT pos or not found
        {
            goldPos = "LEFT";
        } else if(goldPosPixels.x < (detector.downscaleResolution.width/3)*2) {
            goldPos = "MIDDLE";
        } else if(goldPosPixels.x <= detector.downscaleResolution.width)
        {
            goldPos = "RIGHT";
        }

        telemetry.addData("Gold Position", goldPos);
        telemetry.update();

        detector.disable();

        /* Add movements to do stuff */
        motorEncoderRunner.AddMovement(hw.motorMovementRevolutions(hw.liftDrive, hw.LIFT_REVS, 1)); // Raise hook
        motorEncoderRunner.StartMovements(30);
        motorEncoderRunner.Clear();

        motorEncoderRunner.AddMovements(hw.straightMovementInches(-3, 1));
        motorEncoderRunner.StartMovements(30);
        motorEncoderRunner.Clear();

        motorEncoderRunner.AddMovements(hw.driftInches(4, NewHardware.LR_Direction.LEFT, 0.35)); // TODO: Fast as possible, 0.35 works fine
        motorEncoderRunner.StartMovements(30);
        motorEncoderRunner.Clear();

        motorEncoderRunner.AddMovements(hw.straightMovementInches(8, 1));
        motorEncoderRunner.StartMovements(30);
        motorEncoderRunner.Clear();

        if(goldPos == "LEFT")
        {
            motorEncoderRunner.AddMovements(hw.turnDegrees(137, false, 1));
            motorEncoderRunner.StartMovements(30);
            motorEncoderRunner.Clear();

            motorEncoderRunner.AddMovements(hw.straightMovementInches(20, 1));
            motorEncoderRunner.StartMovements(30);
            motorEncoderRunner.Clear();

            motorEncoderRunner.AddMovements(hw.turnDegrees(45, true, 1));
            motorEncoderRunner.StartMovements(30);
            motorEncoderRunner.Clear();

            motorEncoderRunner.AddMovement(hw.motorMovementRevolutions(hw.mineralArmDrive, 1.5, 1));
            motorEncoderRunner.StartMovements(30);
            motorEncoderRunner.Clear();

        } else if(goldPos == "MIDDLE")
        {
            motorEncoderRunner.AddMovements(hw.turnDegrees(97, false, 1));
            motorEncoderRunner.StartMovements(30);
            motorEncoderRunner.Clear();

            motorEncoderRunner.AddMovements(hw.straightMovementInches(19, 1));
            motorEncoderRunner.StartMovements(30);
            motorEncoderRunner.Clear();

            motorEncoderRunner.AddMovement(hw.motorMovementRevolutions(hw.mineralArmDrive, 1.5, 1));
            motorEncoderRunner.StartMovements(30);
            motorEncoderRunner.Clear();
        } else
        {
            // Gold pos == RIGHT
            motorEncoderRunner.AddMovements(hw.turnDegrees(53, false, 1));
            motorEncoderRunner.StartMovements(30);
            motorEncoderRunner.Clear();

            motorEncoderRunner.AddMovements(hw.straightMovementInches(20, 1));
            motorEncoderRunner.StartMovements(30);
            motorEncoderRunner.Clear();

            motorEncoderRunner.AddMovements(hw.turnDegrees(42, false, 1));
            motorEncoderRunner.StartMovements(30);
            motorEncoderRunner.Clear();

            motorEncoderRunner.AddMovement(hw.motorMovementRevolutions(hw.mineralArmDrive, 1.5, 1));
            motorEncoderRunner.StartMovements(30);
            motorEncoderRunner.Clear();
        }

        while(opModeIsActive()) { }

        MotorEncoderRunner.StopAllRunners();
    }
}
