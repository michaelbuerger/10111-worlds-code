package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="DriverPeriod : Iterative OpMode", group="TeleOp")
public class DriverPeriod extends OpMode {

    NewHardware hw = new NewHardware();
    //MotorEncoderRunner motorEncoderRunner = null;

    ElapsedTime runtime = null;
    ElapsedTime timeSinceMineralCollectToggle = null;
    ElapsedTime timeSinceBoxServoToggle = null;
    ElapsedTime boxPositionToggleTimer = null;

    boolean mineralCollectRunning = false;
    final double mineralCollectToggleCooldownSeconds = 0.25;

    boolean boxServoUp = false;
    final double boxServoToggleCooldown = 0.25;

    @Override
    public void init() // Once after driver presses init
    {
        hw.Initialize(hardwareMap, telemetry);
        //motorEncoderRunner = new MotorEncoderRunner();

        runtime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        timeSinceMineralCollectToggle = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        timeSinceBoxServoToggle = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        boxPositionToggleTimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);

        //motorEncoderRunner.AddMovements(hw.straightMovementInches(45, 0.5));

        hw.totemServo.setPosition(hw.UP_TOTEM_POSITION);
        hw.boxServo.setPosition(hw.DOWN_BOX_POSITION);
    }

    @Override
    public void init_loop() // Loop after driver presses init
    {
        telemetry.addLine("Maintaining WiFi Direct Connection");
        telemetry.update();
    }

    @Override
    public void start() // Once after driver presses play
    {
        timeSinceMineralCollectToggle.reset();
        timeSinceBoxServoToggle.reset();
        boxPositionToggleTimer.reset();
    }

    @Override
    public void loop() // Loop after driver presses play
    {
        double fld = 0, frd = 0;
        double bld = 0, brd = 0;

        // Gamepad 1
        double g1_left_joy_x, g1_left_joy_y;
        double g1_right_joy_x, g1_right_joy_y;

        // Gamepad 2
        double g2_left_joy_x, g2_left_joy_y;
        double g2_right_joy_x, g2_right_joy_y;

        g1_left_joy_x = gamepad1.left_stick_x;
        g1_left_joy_y = -gamepad1.left_stick_y;
        g1_right_joy_x = gamepad1.right_stick_x;
        g1_right_joy_y = -gamepad1.right_stick_y;
        g2_left_joy_x = gamepad2.left_stick_x;
        g2_left_joy_y = -gamepad2.left_stick_y;
        g2_right_joy_x = gamepad2.right_stick_x;
        g2_right_joy_y = -gamepad2.right_stick_y;

        // 1 gamepad: driving, mineral arm
        // 2 gamepad: hook, a button toggle, scoring

        // Drive Stuff

        // Straight
        if(Math.abs(g1_left_joy_y) > 0 && Math.abs(g1_left_joy_x) == 0)
        {
            fld = g1_left_joy_y * hw.driveSpeed;
            frd = g1_left_joy_y * hw.driveSpeed;
            bld = g1_left_joy_y * hw.driveSpeed;
            brd = g1_left_joy_y * hw.driveSpeed;
        } else if(Math.abs(g1_left_joy_y) == 0 && Math.abs(g1_left_joy_x) > 0)
        {
            fld = g1_left_joy_x * hw.turnSpeed;
            bld = g1_left_joy_x * hw.turnSpeed;

            frd = -g1_left_joy_x * hw.turnSpeed;
            brd = -g1_left_joy_x * hw.turnSpeed;
        } else if(g1_left_joy_y > 0 && g1_left_joy_x < 0) // Angled turn
        {
            // Forward left
            frd = hw.angledTurnSpeed;
            brd = hw.angledTurnSpeed;

            fld = 0;
            bld = 0;
        } else if(g1_left_joy_y > 0 && g1_left_joy_x > 0)
        {
            // Forward right
            fld = hw.angledTurnSpeed;
            bld = hw.angledTurnSpeed;

            frd = 0;
            brd = 0;

        } else if(g1_left_joy_y < 0 && g1_left_joy_x < 0)
        {
            // Backward left
            fld = -hw.angledTurnSpeed;
            bld = -hw.angledTurnSpeed;

            frd = 0;
            brd = 0;

        } else if(g1_left_joy_y < 0 && g1_left_joy_x > 0)
        {
            // Backward right
            frd = -hw.angledTurnSpeed;
            brd = -hw.angledTurnSpeed;

            fld = 0;
            bld = 0;

        } else if(gamepad1.left_bumper || gamepad1.left_trigger > 0) // Drift
        {
            // Left
            frd = hw.driftSpeed;
            bld = hw.driftSpeed;

            fld = -hw.driftSpeed;
            brd = -hw.driftSpeed;
        } else if(gamepad1.right_bumper || gamepad1.right_trigger > 0)
        {
            // Right
            frd = -hw.driftSpeed;
            bld = -hw.driftSpeed;

            fld = hw.driftSpeed;
            brd = hw.driftSpeed;
        }

        hw.backLeftDrive.setPower(bld);
        hw.backRightDrive.setPower(brd);
        hw.frontLeftDrive.setPower(fld);
        hw.frontRightDrive.setPower(frd);

        // Hook stuff
        if(gamepad2.dpad_up)
        {
            hw.liftDrive.setPower(hw.liftSpeed);
        } else if(gamepad2.dpad_down)
        {
            hw.liftDrive.setPower(-hw.liftSpeed);
        } else
        {
            hw.liftDrive.setPower(0);
        }

        // Mineral arm stuff
        if(Math.abs(g1_right_joy_y) > 0.1)
        {
            hw.mineralArmDrive.setPower(g1_right_joy_y * hw.mineralArmSpeed);
        } else
        {
            hw.mineralArmDrive.setPower(0);
        }

        if(gamepad1.a && timeSinceMineralCollectToggle.seconds() > mineralCollectToggleCooldownSeconds)
        {
            mineralCollectRunning = !mineralCollectRunning;
            timeSinceMineralCollectToggle.reset();
        }

        if(mineralCollectRunning)
        {
            if(gamepad1.b)
            {
                hw.mineralCollectDrive.setPower(-hw.mineralCollectSpeed);
            } else {
                hw.mineralCollectDrive.setPower(hw.mineralCollectSpeed);
            }
        } else
        {
            hw.mineralCollectDrive.setPower(0);
        }

        // Scoring stuff
        if(Math.abs(g2_left_joy_y) > 0.1)
        {
            hw.scoreArmDrive.setPower(g2_left_joy_y * hw.scoreArmSpeed);
        } else
        {
            hw.scoreArmDrive.setPower(0);
        }

        if(gamepad2.a && timeSinceBoxServoToggle.seconds() > boxServoToggleCooldown)
        {
            boxServoUp = !boxServoUp;
            timeSinceBoxServoToggle.reset();
        }

        if(boxServoUp)
        {
            if(boxPositionToggleTimer.milliseconds() < hw.TOTEM_SHAKE_TOGGLE_MILLI) {
                hw.boxServo.setPosition(hw.UP_BOX_POSITION_1);
            }

            if(boxPositionToggleTimer.milliseconds() >= hw.TOTEM_SHAKE_TOGGLE_MILLI && boxPositionToggleTimer.milliseconds() < hw.TOTEM_SHAKE_TOGGLE_MILLI*2) {
                hw.boxServo.setPosition(hw.UP_BOX_POSITION_2);
            }

            if(boxPositionToggleTimer.milliseconds() >= hw.TOTEM_SHAKE_TOGGLE_MILLI*2)
            {
                boxPositionToggleTimer.reset();
            }
        } else
        {
            hw.boxServo.setPosition(hw.DOWN_BOX_POSITION);
            boxPositionToggleTimer.reset();
        }

        /*if((gamepad1.x || gamepad2.x) && timeSinceXPressed.seconds() > 0.25 && !motorEncoderRunner.MotorsBusy())
        {
            motorEncoderRunner.StartMovements(30, false);
        }

        if(motorEncoderRunner.MotorsBusy())
        {
            telemetry.addData("Motor Busy", "True");
        } else
        {
            telemetry.addData("Motor Busy", "False");
            motorEncoderRunner.StopMovements();
        }*/

        hw.totemServo.setPosition(hw.UP_TOTEM_POSITION);

        telemetry.addLine("Maintaining WiFi Direct Connection");
        telemetry.update();
    }

    @Override
    public void stop() // Once after driver presses stop
    {
        MotorEncoderRunner.StopAllRunners();
    }
}
