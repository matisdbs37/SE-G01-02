export interface Question {
    text: string;
    type: 'text' | 'yes_no' | 'slider' | 'select';
    placeholder?: string;
    min?: number;
    max?: number;
    options?: string[];
}

export const QUESTIONS: Question[] = [
    {
        text: "Where are you from ?", type: 'select', placeholder: "Click to select a country", options: []
    },
    {
        text: "Do you agree to receive notifications ?", type:'yes_no'
    },
    {
        text: "On a scale from 0 to 10, how would you rate your current mental health ?", type: 'slider', min: 0, max: 10
    },
    {
        text: "On a scale from 0 to 10, how would you rate the quality of your sleep over the past few days ?", type: 'slider', min: 0, max: 10
    },
    {
        text: "On a scale from 0 to 10, how would you rate your current level of stress ?", type: 'slider', min: 0, max: 10
    },
    {
        text: "On a scale from 0 to 10, how would you rate your experience with meditation ?", type: 'slider', min: 0, max: 10
    }
];