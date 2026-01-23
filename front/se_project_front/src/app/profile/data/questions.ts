// Interface describing the structure of a single questionnaire question
export interface Question {
    text: string; // The question text displayed to the user
    type: 'text' | 'yes_no' | 'slider' | 'select'; // Type of input expected
    placeholder?: string; // Placeholder for text inputs or select dropdown
    min?: number; // Minimum value for sliders
    max?: number; // Maximum value for sliders
    options?: string[]; // Array of options for 'select' type questions
}

// Array of all questions in the questionnaire
export const QUESTIONS: Question[] = [
    {
        text: "On a scale from 0 to 10, how would you rate your current mental health ?", 
        type: 'slider', 
        min: 0, 
        max: 10
    },
    {
        text: "On a scale from 0 to 10, how would you rate the quality of your sleep over the past few days ?", 
        type: 'slider', 
        min: 0, 
        max: 10
    },
    {
        text: "On a scale from 0 to 10, how would you rate your current level of stress ?", 
        type: 'slider', 
        min: 0, 
        max: 10
    },
    {
        text: "On a scale from 0 to 10, how would you rate your experience with meditation ?", 
        type: 'slider', 
        min: 0, 
        max: 10
    }
];