package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MotorEncoderMovement implements Runnable {

    private DcMotor motor = null;
    private double targetPosition;
    private double speed = 1;
    private boolean ignoreMotorCurrentPosition = false;

    private ElapsedTime runtime = new ElapsedTime();
    private Telemetry telemetry = null;

    public MotorEncoderMovement(DcMotor motor, double targetPosition, double speed, Telemetry telemetry)
    {
        this.motor = motor;
        this.targetPosition = targetPosition;
        this.speed = speed;
        this.telemetry = telemetry;
    }

    public MotorEncoderMovement(DcMotor motor, double targetPosition, double speed, Telemetry telemetry, boolean ignoreMotorCurrentPosition)
    {
        this.motor = motor;
        this.targetPosition = targetPosition;
        this.speed = speed;
        this.ignoreMotorCurrentPosition = ignoreMotorCurrentPosition;
        this.telemetry = telemetry;
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

    public void Reset()
    {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void SetUseEncoder()
    {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void Start()
    {
        SetUseEncoder();
        if(ignoreMotorCurrentPosition)
        {
            motor.setTargetPosition((int)targetPosition);
        } else
        {
            motor.setTargetPosition(motor.getCurrentPosition() + (int)targetPosition);
        }

        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(Math.abs(speed));
    }

    public void StartIndividual(double cutoffTimeSeconds) // DON'T USE THIS
    {
        SetUseEncoder();
        Start();

        runtime.reset();

        while(motor.isBusy())
        {
            if(runtime.seconds() >= cutoffTimeSeconds)
            {
                break;
            }
        }

        Stop();
        SetUseNoEncoder();
    }

    public void StartIndividual()
    {
        SetUseEncoder();
        Start();

        runtime.reset();

        while(motor.isBusy())
        {
            if(runtime.seconds() >= 30)
            {
                break;
            }
        }

        Stop();
        SetUseNoEncoder();
    }

    /* Runnable required implementation, runs movement on new thread (no cutoff time) */
    public void run()
    {
        StartIndividual();
        AddTelemetry("Running motor movement on new thread");
    }

    /* Call when motors are no longer busy or runtime is beyond cutoff */
    public void Stop()
    {
        motor.setPower(0);
    }

    public void SetUseNoEncoder()
    {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public boolean isBusy()
    {
        return !(Math.abs(motor.getCurrentPosition()-motor.getTargetPosition()) < 50);
    }

}
