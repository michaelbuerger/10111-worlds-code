package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

import java.util.ArrayList;

public class NewHardware {
    public DcMotor liftDrive;
    public DcMotor backLeftDrive;
    public DcMotor backRightDrive;
    public DcMotor frontLeftDrive;
    public DcMotor frontRightDrive;
    public DcMotor mineralArmDrive;
    public DcMotor mineralCollectDrive;
    public DcMotor scoreArmDrive;

    public Servo totemServo;
    public Servo boxServo;

    //public WebcamName sampleCamera;

    public final double liftSpeed = 1;
    public final double driveSpeed = 0.85;
    public final double turnSpeed = 1;
    public final double angledTurnSpeed = 1;
    public final double mineralArmSpeed = 1;
    public final double mineralCollectSpeed = 0.5;
    public final double scoreArmSpeed = 1;
    public final double driftSpeed = 1;

    public final double UP_TOTEM_POSITION = 0.5;
    public final double DOWN_TOTEM_POSITION = 1;
    public final double UP_BOX_POSITION_1 = 0.65;
    public final double UP_BOX_POSITION_2 = 0.50;
    public final double DOWN_BOX_POSITION = 0.12;
    public final double TOTEM_SHAKE_TOGGLE_MILLI = 500;

    public enum LR_Direction
    {
        LEFT, RIGHT
    }

    public HardwareMap hwMap;
    private Telemetry telemetry = null;

    /* --- Specifically configured to 10111 (2018-2019) Worlds drive train --- */
    public final int WHEEL_DIAMETER_INCHES = 4;
    public final double LIFT_REVS = 5.15; // How many revolutions the lift motor must turn to bring the bot down or up from hook
    /* --- Specifically configured to 10111 (2018-2019) Worlds drive train --- */

    public void Initialize(HardwareMap hwMap, Telemetry telemetry)
    {
        this.hwMap = hwMap;
        this.telemetry = telemetry;

        liftDrive = hwMap.get(DcMotor.class, "liftDrive");
        backLeftDrive = hwMap.get(DcMotor.class,"backLeftDrive");
        backRightDrive = hwMap.get(DcMotor.class,"backRightDrive");
        frontLeftDrive = hwMap.get(DcMotor.class,"frontLeftDrive");
        frontRightDrive = hwMap.get(DcMotor.class,"frontRightDrive");
        mineralArmDrive = hwMap.get(DcMotor.class, "mineralArmDrive");
        mineralCollectDrive = hwMap.get(DcMotor.class, "mineralCollectDrive");
        scoreArmDrive = hwMap.get(DcMotor.class, "scoreArmDrive");

        totemServo = hwMap.get(Servo.class, "totemServo");
        boxServo = hwMap.get(Servo.class, "boxServo");

        //sampleCamera = hwMap.get(WebcamName.class, "Sample Camera");

        liftDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        frontLeftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        mineralArmDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        mineralCollectDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        scoreArmDrive.setDirection(DcMotorSimple.Direction.FORWARD);

        totemServo.setDirection(Servo.Direction.FORWARD);
        boxServo.setDirection(Servo.Direction.FORWARD);
    }

    private void AddTelemetry(String s1)
    {
        if(telemetry != null)
        {
            telemetry.addLine(s1);
            telemetry.update();
        }
    }

    private void AddTelemetry(String caption, String value)
    {
        if(telemetry != null)
        {
            telemetry.addData(caption, value);
            telemetry.update();
        }
    }

    public double inchesToTicks(double inches, MotorConfigurationType motorType)
    {
        return (inches / (WHEEL_DIAMETER_INCHES * Math.PI)) * motorTicksPerRev(motorType);
    }

    public double motorTicksPerRev(MotorConfigurationType motorType)
    {
        return motorType.getTicksPerRev();
    }

    public MotorEncoderMovement motorMovementRevolutions(DcMotor motor, double revolutions, double speed)
    {
        return new MotorEncoderMovement(motor, motorTicksPerRev(motor.getMotorType())*revolutions, speed, this.telemetry);
    }

    /* Alternatives to FTC SDK functions to fix disconnect issues */

    public void activeSleep(long length, String msg) // Milliseconds
    {
        ElapsedTime timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        while(timer.milliseconds() <= length)
        {

        }
    }

    /* Alternatives to FTC SDK functions to fix disconnect issues */

    /* --- Specifically configured to 10111 (2018-2019) Worlds drive train --- */
    public ArrayList<MotorEncoderMovement> straightMovementInches(double inches, double speed)
    {
        ArrayList<MotorEncoderMovement> motorEncoderMovements = new ArrayList<>();

        double backLeftTicks = inchesToTicks(inches, backRightDrive.getMotorType());
        double backRightTicks = inchesToTicks(inches, backLeftDrive.getMotorType());
        double frontLeftTicks = inchesToTicks(inches, backRightDrive.getMotorType());
        double frontRightTicks = inchesToTicks(inches, backLeftDrive.getMotorType());

        motorEncoderMovements.add(new MotorEncoderMovement(backLeftDrive, backLeftTicks, speed, this.telemetry));
        motorEncoderMovements.add(new MotorEncoderMovement(backRightDrive, backRightTicks, speed, this.telemetry));
        motorEncoderMovements.add(new MotorEncoderMovement(frontLeftDrive, frontLeftTicks, speed, this.telemetry));
        motorEncoderMovements.add(new MotorEncoderMovement(frontRightDrive, frontRightTicks, speed, this.telemetry));

        return motorEncoderMovements;
    }

    public ArrayList<MotorEncoderMovement> turnDegrees(double degrees, boolean clockwise, double speed)
    {
        ArrayList<MotorEncoderMovement> motorEncoderMovements = new ArrayList<>();

        double leftSign = 1;
        double rightSign = -1;

        if(!clockwise)
        {
            leftSign = -1;
            rightSign = 1;
        }

        double backLeftTicks = motorTicksPerRev(backLeftDrive.getMotorType()) * degrees/90;
        double backRightTicks = motorTicksPerRev(backRightDrive.getMotorType()) * degrees/90;
        double frontLeftTicks = motorTicksPerRev(frontLeftDrive.getMotorType()) * degrees/90;
        double frontRightTicks = motorTicksPerRev(frontRightDrive.getMotorType()) * degrees/90;

        motorEncoderMovements.add(new MotorEncoderMovement(backLeftDrive, backLeftTicks*1.45*leftSign, speed, this.telemetry));
        motorEncoderMovements.add(new MotorEncoderMovement(backRightDrive, backRightTicks*1.45*rightSign, speed, this.telemetry));
        motorEncoderMovements.add(new MotorEncoderMovement(frontLeftDrive, frontLeftTicks*1.45*leftSign, speed, this.telemetry));
        motorEncoderMovements.add(new MotorEncoderMovement(frontRightDrive, frontRightTicks*1.45*rightSign, speed, this.telemetry));

        return motorEncoderMovements;
    }

    public ArrayList<MotorEncoderMovement> driftInches(double inches, LR_Direction direction, double speed)
    {
        ArrayList<MotorEncoderMovement> motorEncoderMovements = new ArrayList<>();

        double backLeftTicks = inchesToTicks(inches, backLeftDrive.getMotorType());
        double backRightTicks = inchesToTicks(inches, backRightDrive.getMotorType());
        double frontLeftTicks = inchesToTicks(inches, frontLeftDrive.getMotorType());
        double frontRightTicks = inchesToTicks(inches, frontRightDrive.getMotorType());

        switch(direction)
        {
            case LEFT:
                frontLeftTicks *= -1;
                backRightTicks *= -1;
                break;

            case RIGHT:
                frontRightTicks *= -1;
                backLeftTicks *= -1;
                break;
        }

        motorEncoderMovements.add(new MotorEncoderMovement(backLeftDrive, backLeftTicks*4, speed, this.telemetry));
        motorEncoderMovements.add(new MotorEncoderMovement(backRightDrive, backRightTicks*4, speed, this.telemetry));
        motorEncoderMovements.add(new MotorEncoderMovement(frontLeftDrive, frontLeftTicks*4, speed, this.telemetry));
        motorEncoderMovements.add(new MotorEncoderMovement(frontRightDrive, frontRightTicks*4, speed, this.telemetry));

        return motorEncoderMovements;
    }
    /* --- Specifically configured to 10111 (2018-2019) Worlds drive train --- */

}
