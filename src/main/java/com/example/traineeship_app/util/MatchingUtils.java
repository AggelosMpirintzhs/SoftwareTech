package com.example.traineeship_app.util;

import com.example.traineeship_app.domainmodel.Student;
import com.example.traineeship_app.domainmodel.TraineeshipPosition;

import java.util.Arrays;
import java.util.List;

public class MatchingUtils {

    public static int countMatchedSkills(Student student, TraineeshipPosition position) {
        List<String> positionSkills = Arrays.stream(position.getSkills().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        List<String> studentSkills = student.getSkills().stream()
                .map(String::trim)
                .toList();

        int matched = 0;
        for (String skill : positionSkills) {
            if (studentSkills.contains(skill)) {
                matched++;
            }
        }

        return matched;
    }

    public static int countMatchedInterests(Student student, TraineeshipPosition position) {
        List<String> positionInterests = Arrays.stream(position.getTopics().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        List<String> studentInterests = student.getInterests().stream()
                .map(String::trim)
                .toList();

        int matched = 0;
        for (String interest : positionInterests) {
            if (studentInterests.contains(interest)) {
                matched++;
            }
        }

        return matched;
    }


    public static boolean isLocationMatch(Student student, TraineeshipPosition position) {
        return student.getPreferredLocation().equalsIgnoreCase(
                position.getCompany().getCompanyLocation()
        );
    }

    public static int calculateTotalScore(Student student, TraineeshipPosition position) {
        int totalSkills = position.getSkills().split(",").length;
        int totalInterests = position.getTopics().split(",").length;

        int skillsScore = totalSkills == 0 ? 0 :
                (int) (((double) countMatchedSkills(student, position) / totalSkills) * 50);

        int interestsScore = totalInterests == 0 ? 0 :
                (int) (((double) countMatchedInterests(student, position) / totalInterests) * 30);

        int locationScore = isLocationMatch(student, position) ? 20 : 0;

        return skillsScore + interestsScore + locationScore;
    }

    public static boolean studentCoversAllRequiredSkills(Student student, TraineeshipPosition position) {
        List<String> requiredSkills = Arrays.stream(position.getSkills().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        List<String> studentSkills = student.getSkills().stream()
                .map(String::trim)
                .toList();

        for (String requiredSkill : requiredSkills) {
            if (!studentSkills.contains(requiredSkill)) {
                return false;
            }
        }

        return true;
    }
}
