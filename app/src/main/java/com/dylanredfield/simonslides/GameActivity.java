package com.dylanredfield.simonslides;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class GameActivity extends Activity {
    private TextView mScoreCounter;
    private ImageView mDirectionImage;
    private int mColorNumber;
    private int mDirectionNumber;
    private Random mGenerator;
    private GestureDetectorCompat g;
    private android.os.Handler handler;
    private android.os.Handler countHandler;
    private Handler tutorialHandler;
    private Runnable tutorialRun;
    private boolean runAgain = false;
    private boolean enabled = false;
    private Runnable testRun;
    private Context mContext;
    private int mScore;
    private int mTime;
    private int mPreviousColor;
    private int mPreviousDirection;
    private SharedPreferences mPref;
    private int countDown;
    private Runnable countRun;
    private TextView mCountDownText;
    private boolean mFirstTime;
    private int tutorialCount;
    private TextView mTutorialText;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_game);
        mPref = getSharedPreferences(MainActivity.PREF_STRING, Activity.MODE_PRIVATE);
        mColorNumber = 0;
        mDirectionNumber = 0;
        tutorialCount = 4;

        if (mContext == null) {
            mContext = this;
        }

        countDown = 3;
        mTime = getIntent().getIntExtra(MainActivity.SPEED_CONSTANT, 1000);
        mFirstTime = mPref.getBoolean(MainActivity.FIRST_TIME_STRING, true);

        makeViews();
        makeRunables();
        mScoreCounter.setText("" + mScore);


        mGenerator = new Random();
        g = new GestureDetectorCompat(getApplicationContext(), new OnSwipeListener());
        handler = new android.os.Handler();
        countHandler = new Handler();

        if (mFirstTime) {
            tutorial();

        } else {
            mCountDownText.setText("" + countDown);
            countDown--;
            countHandler.postDelayed(countRun, 1000);
        }


    }

    public void tutorial() {
        mPref.edit().putBoolean(MainActivity.FIRST_TIME_STRING, false).commit();

        tutorialHandler = new Handler();
        tutorialRun = new Runnable() {
            @Override
            public void run() {
                if (tutorialCount == 3) {
                    mDirectionImage.setImageDrawable(getResources()
                            .getDrawable(R.drawable.red));
                    mTutorialText
                            .setText("If the arrow is RED swipe in the OPPOSITE direction!\n\n" +
                                    "High Scores will only count in normal or hard mode!");
                    tutorialCount--;
                    tutorialHandler.postDelayed(tutorialRun, 3000);
                } else if (tutorialCount == 2) {
                    mDirectionImage.setImageDrawable(getResources()
                            .getDrawable(R.drawable.grey));
                    mTutorialText.setText("If the arrow is GREY then DO NOTHING!\n\n" +
                            "High Scores will only count in normal or hard mode!");
                    tutorialCount--;
                    tutorialHandler.postDelayed(tutorialRun, 3000);
                } else if (tutorialCount == 1) {
                    mDirectionImage.setImageDrawable(null);
                    mScoreCounter.setText("" + mScore);
                    mTutorialText.setVisibility(View.GONE);
                    mCountDownText.setText("" + countDown);
                    countDown--;
                    countHandler.postDelayed(countRun, 1000);
                } else if (tutorialCount == 0) {

                }

            }
        };

        mDirectionImage.setImageDrawable(getResources().getDrawable(R.drawable.green));
        mTutorialText.setVisibility(View.VISIBLE);
        mTutorialText.setText("If the arrow is GREEN swipe in the SAME direction!\n\n" +
                "High Scores will only count in normal or hard mode!");
        mScoreCounter.setText("Tutorial");
        tutorialCount--;
        tutorialHandler.postDelayed(tutorialRun, 3000);


    }

    public void makeViews() {
        mDirectionImage = (ImageView) findViewById(R.id.direction_image);
        mScoreCounter = (TextView) findViewById(R.id.score_counter);
        mCountDownText = (TextView) findViewById(R.id.count_down);
        mTutorialText = (TextView) findViewById(R.id.tutorial_text);
    }

    public void makeRunables() {
        testRun = new Runnable() {
            @Override
            public void run() {
                if (mColorNumber == 4) {
                    mScore++;
                    mScoreCounter.setText("" + mScore);
                    runImage();
                } else {
                    handler.removeCallbacks(testRun);
                    enabled = false;
                    int tempScore;
                    tempScore = mPref.getInt(MainActivity.MEDIUM_STRING, 0);
                    boolean newHighScore = false;

                    if (mScore > tempScore && mTime != 1250) {

                        SharedPreferences.Editor editor = mPref.edit();
                        editor.putInt(MainActivity.MEDIUM_STRING, mScore);
                        newHighScore = true;

                        editor.commit();
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Score: " + mScore)
                            .setNegativeButton("Go Home", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setPositiveButton("Play Again", new DialogInterface
                                    .OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    int tempScore;
                                    tempScore = mPref.getInt(MainActivity.MEDIUM_STRING, 0);

                                    playAgain();
                                }
                            });
                    if (newHighScore) {
                        builder.setMessage("Score: " + mScore + "\n\nNew High Score!");
                    }
                    builder.setTitle("Too Slow!");
                    builder.setCancelable(false);
                    AlertDialog alertB = builder.create();
                    alertB.show();
                }
            }
        };

        countRun = new Runnable() {
            @Override
            public void run() {
                if (countDown > 0) {
                    mCountDownText.setVisibility(View.VISIBLE);
                    mCountDownText.setText("" + countDown);
                    countDown--;
                    countHandler.postDelayed(countRun, 1000);
                } else if (countDown == 0) {
                    mCountDownText.setText("GO!");
                    countDown--;
                    countHandler.postDelayed(countRun, 1000);
                } else if (countDown == -1) {
                    mCountDownText.setVisibility(View.GONE);
                    enabled = true;
                    runImage();
                }

            }
        };
    }

    public void runImage() {
        setImage();
        setDirection();


        runAgain = false;
        handler.postDelayed(testRun, mTime);
    }


    private void setImage() {
        mPreviousColor = mColorNumber;
        mColorNumber = mGenerator.nextInt(5);
        if (mColorNumber == 0 || mColorNumber == 1) {
            mDirectionImage.setImageDrawable(getResources().getDrawable(R.drawable.green));
        } else if (mColorNumber == 2 || mColorNumber == 3) {
            mDirectionImage.setImageDrawable(getResources().getDrawable(R.drawable.red));
        } else {
            mDirectionImage.setImageDrawable(getResources().getDrawable(R.drawable.grey));
        }
    }

    private void setDirection() {
        mPreviousDirection = mDirectionNumber;
        mDirectionNumber = mGenerator.nextInt(4);
        if (mPreviousDirection == mDirectionNumber) {
            if (mPreviousColor == 1 || mPreviousColor == 0) {
                if (mColorNumber == 0 || mColorNumber == 1) {
                    mDirectionNumber = (mDirectionNumber += 1) % 4;
                }
            } else if (mPreviousColor == 2 || mPreviousColor == 3) {
                if (mColorNumber == 2 || mColorNumber == 4) {
                    mDirectionNumber = (mDirectionNumber += 1) % 4;
                }
            } else if (mPreviousColor == 4 && mColorNumber == 4) {
                mDirectionNumber = (mDirectionNumber += 1) % 4;
            }
        }
        if (mDirectionNumber == 0) {
            mDirectionImage.setRotation(0);
        } else if (mDirectionNumber == 1) {
            mDirectionImage.setRotation(90);
        } else if (mDirectionNumber == 2) {
            mDirectionImage.setRotation(180);
        } else {
            mDirectionImage.setRotation(270);
        }
    }

    public void playAgain() {
        mDirectionImage.setImageResource(android.R.color.transparent);
        mScore = 0;
        mScoreCounter.setText("" + mScore);
        enabled = false;
        countDown = 3;
        mCountDownText.setText("" + countDown);
        mCountDownText.setVisibility(View.VISIBLE);
        countDown--;
        countHandler.postDelayed(countRun, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(testRun);
        countHandler.removeCallbacks(countRun);
        if (tutorialHandler != null) {
            tutorialHandler.removeCallbacks(tutorialRun);
        }
        int tempScore;
        tempScore = mPref.getInt(MainActivity.MEDIUM_STRING, 0);

        if (mScore > tempScore && mTime != 1250) {
            SharedPreferences.Editor editor = mPref.edit();
            editor.putInt(MainActivity.MEDIUM_STRING, tempScore);
            editor.commit();
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return g.onTouchEvent(event);
    }


    public class OnSwipeListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // Grab two events located on the plane at e1=(x1, y1) and e2=(x2, y2)
            // Let e1 be the initial event
            // e2 can be located at 4 different positions, consider the following diagram
            // (Assume that lines are separated by 90 degrees.)
            //
            //
            //         \ A  /
            //          \  /
            //       D   e1   B
            //          /  \
            //         / C  \
            //
            // So if (x2,y2) falls in region:
            //  A => it's an UP swipe
            //  B => it's a RIGHT swipe
            //  C => it's a DOWN swipe
            //  D => it's a LEFT swipe
            //

            float x1 = e1.getX();
            float y1 = e1.getY();

            float x2 = e2.getX();
            float y2 = e2.getY();

            Direction direction = getDirection(x1, y1, x2, y2);
            return onSwipe(direction);
        }

        public boolean onSwipe(Direction direction) {
            if (enabled) {
                enabled = false;
                if (direction == Direction.down) {
                    if (mDirectionNumber == 2) {
                        if (mColorNumber == 0 || mColorNumber == 1) {
                            runAgain = true;
                        }

                    } else if (mDirectionNumber == 0) {
                        if (mColorNumber == 2 || mColorNumber == 3) {
                            runAgain = true;
                        }
                    }

                } else if (direction == Direction.up) {
                    if (mDirectionNumber == 0) {
                        if (mColorNumber == 0 || mColorNumber == 1) {
                            runAgain = true;
                        }

                    } else if (mDirectionNumber == 2) {
                        if (mColorNumber == 2 || mColorNumber == 3) {
                            runAgain = true;
                        }
                    }
                } else if (direction == Direction.left) {
                    if (mDirectionNumber == 3) {
                        if (mColorNumber == 0 || mColorNumber == 1) {
                            runAgain = true;
                        }
                    } else if (mDirectionNumber == 1) {
                        if (mColorNumber == 2 || mColorNumber == 3) {
                            runAgain = true;
                        }
                    }
                } else if (direction == Direction.right) {
                    if (mDirectionNumber == 1) {
                        if (mColorNumber == 0 || mColorNumber == 1) {
                            runAgain = true;
                        }
                    } else if (mDirectionNumber == 3) {
                        if (mColorNumber == 2 || mColorNumber == 3) {
                            runAgain = true;
                        }
                    }
                }
                if (runAgain) {
                    handler.removeCallbacks(testRun);
                    enabled = true;
                    mScore++;
                    mScoreCounter.setText("" + mScore);
                    runImage();
                } else {

                    handler.removeCallbacks(testRun);
                    int tempScore;
                    tempScore = mPref.getInt(MainActivity.MEDIUM_STRING, 0);
                    boolean newHighScore = false;

                    if (mScore > tempScore && mTime != 1250) {
                        SharedPreferences.Editor editor = mPref.edit();
                        editor.putInt(MainActivity.MEDIUM_STRING, mScore);
                        newHighScore = false;
                        editor.commit();
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Score: " + mScore)
                            .setNegativeButton("Go Home", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setPositiveButton("Play Again",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            int tempScore;
                                            tempScore = mPref
                                                    .getInt(MainActivity.MEDIUM_STRING, 0);

                                            playAgain();
                                        }
                                    });
                    if (newHighScore) {
                        builder.setMessage("Score: " + mScore + "\n\nNew High Score!");
                    }
                    builder.setTitle("Game Over!");
                    builder.setCancelable(false);
                    AlertDialog alertB = builder.create();
                    alertB.show();
                }
            }
            return false;
        }

        /**
         * Given two points in the plane p1=(x1, x2) and p2=(y1, y1), this method
         * returns the direction that an arrow pointing from p1 to p2 would have.
         *
         * @param x1 the x position of the first point
         * @param y1 the y position of the first point
         * @param x2 the x position of the second point
         * @param y2 the y position of the second point
         * @return the direction
         */
        public Direction getDirection(float x1, float y1, float x2, float y2) {
            double angle = getAngle(x1, y1, x2, y2);
            return Direction.get(angle);
        }

        /**
         * Finds the angle between two points in the plane (x1,y1) and (x2, y2)
         * The angle is measured with 0/360 being the X-axis to the right, angles
         * increase counter clockwise.
         *
         * @param x1 the x position of the first point
         * @param y1 the y position of the first point
         * @param x2 the x position of the second point
         * @param y2 the y position of the second point
         * @return the angle between two points
         */
        public double getAngle(float x1, float y1, float x2, float y2) {

            double rad = Math.atan2(y1 - y2, x2 - x1) + Math.PI;
            return (rad * 180 / Math.PI + 180) % 360;
        }


    }

    public enum Direction {
        up,
        down,
        left,
        right;

        /**
         * Returns a direction given an angle.
         * Directions are defined as follows:
         * <p/>
         * Up: [45, 135]
         * Right: [0,45] and [315, 360]
         * Down: [225, 315]
         * Left: [135, 225]
         *
         * @param angle an angle from 0 to 360 - e
         * @return the direction of an angle
         */
        public static Direction get(double angle) {
            if (inRange(angle, 45, 135)) {
                return Direction.up;
            } else if (inRange(angle, 0, 45) || inRange(angle, 315, 360)) {
                return Direction.right;
            } else if (inRange(angle, 225, 315)) {
                return Direction.down;
            } else {
                return Direction.left;
            }

        }

        /**
         * @param angle an angle
         * @param init  the initial bound
         * @param end   the final bound
         * @return returns true if the given angle is in the interval [init, end).
         */
        private static boolean inRange(double angle, float init, float end) {
            return (angle >= init) && (angle < end);
        }
    }
}
