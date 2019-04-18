package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Disabled
@Autonomous(name="TurnTest90 : Linear OpMode", group="Autonomous")
public class TurnTest90 extends LinearOpMode {

    NewHardware hw = new NewHardware();
    MotorEncoderRunner motorEncoderRunner = null;

    ElapsedTime runtime = null;

    @Override
    public void runOpMode() /* Run auto code */
    {
        hw.Initialize(hardwareMap, telemetry);
        motorEncoderRunner = new MotorEncoderRunner(this);

        hw.totemServo.setPosition(hw.UP_TOTEM_POSITION);

        runtime = new ElapsedTime();

        waitForStart();
        runtime.reset();

        //hw.totemServo.setPosition(hw.DOWN_TOTEM_POSITION);

            motorEncoderRunner.AddMovements(hw.turnDegrees(90, false, 1));
            motorEncoderRunner.StartMovements(30);
            motorEncoderRunner.Clear();
        while(opModeIsActive()) { }

        MotorEncoderRunner.StopAllRunners();
    }
}
